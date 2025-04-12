package com.tinyx.service;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.tinyx.ErrorCodes;
import com.tinyx.Operation;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.redis.UserQuery;
import com.tinyx.repository.UserPublisher;
import com.tinyx.repository.UserRepository;
import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.converter.UserContractToUserEntityConverter;
import com.tinyx.user.converter.UserEntityToLightContractConverter;
import com.tinyx.user.converter.UserEntityToUserContractConverter;
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
  @Inject UserEntityToUserContractConverter userEntityToUserContractConverter;
  @Inject UserContractToUserEntityConverter userContractToUserEntityConverter;
  @Inject UserEntityToLightContractConverter userEntityToLightContractConverter;

  public LightUserContract createUser(String userName) {
    UserEntity newUser = new UserEntity(UUID.randomUUID(), userName, ZonedDateTime.now());
    try {
      userRepository.persist(newUser);
      userPublisher.post(newUser, UserQuery.Operation.CREATE);
    } catch (MongoWriteException e) {
      ErrorCodes.DUPLICATE_KEY.throwError(userName);
    }

    return userEntityToLightContractConverter.convert(newUser);
  }

  public LightUserContract getUserByName(String userName) {
    UserEntity user =
        userRepository
            .findByName(userName)
            .orElseThrow(ErrorCodes.USER_NOT_FOUND.asSupplier(userName));
    return userEntityToLightContractConverter.convert(user);
  }

  public UserContract getUserById(UUID userId) {
    UserEntity user =
        userRepository
            .findByIdOptional(userId)
            .orElseThrow(ErrorCodes.USER_NOT_FOUND.asSupplier(userId));
    return userEntityToUserContractConverter.convert(user);
  }

  public List<UserContract> getUsersById(List<UUID> userIds) {
    Bson filter = Filters.in("_id", userIds);
    List<UserContract> users =
        userRepository
            .mongoCollection()
            .find(filter)
            .map(doc -> userEntityToUserContractConverter.convert(doc))
            .into(new ArrayList<>());
    if (users.isEmpty()) ErrorCodes.USERS_NOT_FOUND.throwError(userIds);
    return users;
  }

  public void handleUserMongoWriteOperation(
      HashMap<UUID, ArrayList<UUID>> map, Operation oper, String fieldName) {
    mongoUtils.handleMongoWriteOperationGeneric(
        map, oper, fieldName, userRepository.mongoCollection());
  }
}
