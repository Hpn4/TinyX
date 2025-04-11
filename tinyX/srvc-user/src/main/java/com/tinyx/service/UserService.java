package com.tinyx.service;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.tinyx.ErrorCodes;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.redis.UserQuery;
import com.tinyx.repository.UserPublisher;
import com.tinyx.repository.UserRepository;
import com.tinyx.user.UserConverter;
import com.tinyx.user.UserEntityToLightContractConverter;
import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.*;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;
  @Inject UserPublisher userPublisher;
  @Inject Logger logger;
  @Inject MongoUtils mongoUtils;
  @Inject UserConverter userConverter;
  @Inject UserEntityToLightContractConverter userEntityToLightContractConverter;

  /**
   * Represents the possible operations that can be done on a user in the Mongo USER collection.
   *
   * <p>Can be either ADD (adding UUIDs to the list containing blocked users) or DELETE (removing
   * UUIDs from the list containing blocked users).
   */
  public enum UserOperation {
    ADD,
    DELETE
  }

  public UserContract createUser(String userName) {
    UserEntity newUser = new UserEntity(UUID.randomUUID(), userName, ZonedDateTime.now());
    try {
      userRepository.persist(newUser);
      userPublisher.post(newUser, UserQuery.Operation.CREATE);
    } catch (MongoWriteException e) {
      ErrorCodes.DUPLICATE_KEY.throwError(userName);
    }
    return userConverter.convertUser(newUser);
  }

  public LightUserContract getUserByName(String userName) {
    UserEntity user =
        userRepository
            .findByName(userName)
            .orElseThrow(ErrorCodes.USER_NOT_FOUND.asSupplier(userName));
    return userEntityToLightContractConverter.convertUser(user);
  }

  public UserContract getUserById(UUID userId) {
    UserEntity user =
        userRepository
            .findByIdOptional(userId)
            .orElseThrow(ErrorCodes.USER_NOT_FOUND.asSupplier(userId));
    return userConverter.convertUser(user);
  }

  public List<UserContract> getUsersById(List<UUID> userIds) {
    Bson filter = Filters.in("_id", userIds);
    List<UserContract> users =
        userRepository
            .mongoCollection()
            .find(filter)
            .map(doc -> userConverter.convertUser(doc))
            .into(new ArrayList<>());
    if (users.isEmpty()) ErrorCodes.USERS_NOT_FOUND.throwError(userIds);
    return users;
  }

  /**
   * Bulk updates users in the USER Mongo collection, either to add UUIDs to their fieldName list,
   * or remove some.
   *
   * @param map A HashMap where each key corresponds to a user that needs to be updated, the value
   *     being the list of UUIDs to add or remove from the fieldName list.
   * @param oper The operation to execute for each user (adding or removing from the fieldName
   *     list).
   * @param fieldName The field from mongo to update
   */
  public void handleMongoWriteOperation(
      HashMap<UUID, ArrayList<UUID>> map, UserOperation oper, String fieldName) {
    ArrayList<WriteModel<UserEntity>> operations = new ArrayList<>();

    for (Map.Entry<UUID, ArrayList<UUID>> entry : map.entrySet()) {
      ArrayList<UUID> newEntries = entry.getValue();

      if (newEntries == null || newEntries.isEmpty()) {
        logger.warn("No new elements found for " + entry.getKey() + ", unexpected behavior.");
        continue;
      }

      Bson filter = Filters.eq("_id", entry.getKey());
      Bson update =
          oper == UserOperation.ADD
              ? Updates.addEachToSet(fieldName, newEntries)
              : Updates.pullAll(fieldName, newEntries);

      operations.add(new UpdateOneModel<>(filter, update));
    }

    mongoUtils.BulkWriteOperations(operations, userRepository.mongoCollection());
  }
}
