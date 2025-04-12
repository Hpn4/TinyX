package com.tinyx.repository;

import com.mongodb.client.model.*;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.timeline.entity.UserTimelineEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bson.conversions.Bson;

@ApplicationScoped
public class UserTimelineRepository
    implements PanacheMongoRepositoryBase<UserTimelineEntity, UUID> {

  @Inject MongoUtils mongoUtils;

  /**
   * Take a list of user IDs (UUID) and register an empty timeline for each one of them. If a user
   * already has a timeline, it's not touched. The operation is batched
   *
   * @param userIds The list of user UUIDs
   */
  public void saveUsers(List<UUID> userIds) {
    mongoUtils.insert(
        userIds.stream().map(id -> new UserTimelineEntity(id, new ArrayList<>())),
        mongoCollection());
  }

  /**
   * Add a list of posts for each user. Operations are batched and duplicates are removed. In case
   * of errors (a user does not exist, invalid UUID) insertions keeps going
   *
   * @param userTimelineEntities A map with in key a user UUID and in value entries to be added.
   */
  public void addToUsers(
      Map<UUID, List<UserTimelineEntity.UserTimelinePostEntry>> userTimelineEntities) {
    List<WriteModel<UserTimelineEntity>> writeModels = new ArrayList<>();

    for (Map.Entry<UUID, List<UserTimelineEntity.UserTimelinePostEntry>> entry :
        userTimelineEntities.entrySet()) {

      if (entry.getValue().isEmpty()) continue;

      Bson filter = Filters.eq("_id", entry.getKey());
      Bson update = Updates.addEachToSet("posts", entry.getValue());

      writeModels.add(new UpdateOneModel<>(filter, update));
    }

    if (writeModels.isEmpty()) return;

    mongoUtils.bulkWriteOperations(writeModels, mongoCollection());
  }

  /**
   * Remove a list of posts for each user. Operations are batched and duplicates are removed. In
   * case of errors (a user does not exist, invalid UUID) insertions keeps going
   *
   * @param entriesToRemovePerUser A map with in key a user UUID and in value entries to be removed.
   */
  public void removeForUsers(Map<UUID, List<UUID>> entriesToRemovePerUser) {
    List<WriteModel<UserTimelineEntity>> writeModels = new ArrayList<>();

    for (Map.Entry<UUID, List<UUID>> entry : entriesToRemovePerUser.entrySet()) {
      if (entry.getValue().isEmpty()) continue;

      Bson filter = Filters.eq("_id", entry.getKey());
      Bson update = Updates.pull("posts", Filters.in("_id", entry.getValue()));

      writeModels.add(new UpdateOneModel<>(filter, update));
    }

    if (writeModels.isEmpty()) return;

    mongoUtils.bulkWriteOperations(writeModels, mongoCollection());
  }

  /**
   * Removes the given list of post UUIDs from all users
   *
   * @param postIds The postID to removed
   */
  public void removeFromAllUsers(List<UUID> postIds) {
    List<WriteModel<UserTimelineEntity>> writeModels = new ArrayList<>();

    writeModels.add(
        new UpdateManyModel<>(
            Filters.in("posts._id", postIds), Updates.pull("posts", Filters.in("_id", postIds))));

    mongoUtils.bulkWriteOperations(writeModels, mongoCollection());
  }
}
