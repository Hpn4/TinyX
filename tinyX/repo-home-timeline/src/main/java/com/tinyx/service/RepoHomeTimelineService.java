package com.tinyx.service;

import com.mongodb.client.model.*;
import com.tinyx.home.entity.HomeTimelineMongoEntity;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.repository.RepoHomeTimelineRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RepoHomeTimelineService {

  @Inject Logger logger;

  @Inject MongoUtils mongoUtils;

  /**
   * Represents the possible operations that can be done on a user in the HomeTimeline Mongo
   * collection.
   *
   * <p>Can be either ADD (adding UUIDs to the timeline) or DELETE (removing UUIDs from the
   * timeline).
   */
  public enum HomeTimelineOperation {
    ADD,
    DELETE
  }

  @Inject RepoHomeTimelineRepository repository;

  /**
   * Adds one or more newly created users to the HomeTimeline Mongo collection, with an empty list
   * as timelineIds.
   *
   * @param users The users' UUIDs to add.
   */
  public void InitializeUsersHomeTimelines(List<UUID> users) {
    mongoUtils.Insert(
        users.stream().map(u -> new HomeTimelineMongoEntity(u, new ArrayList<>())),
        repository.mongoCollection());
  }

  /**
   * Bulk updates users in the HomeTimeline Mongo collection, either to add UUIDs to their timeline,
   * or remove some.
   *
   * @param map A HashMap where each key corresponds to a user that needs to be updated, the value
   *     being the list of UUIDs to add or remove from the timeline.
   * @param oper The operation to execute for each user (adding or removing from the timeline).
   */
  public void HandleOperationsHomeTimeline(
      HashMap<UUID, ArrayList<UUID>> map, HomeTimelineOperation oper) {
    ArrayList<WriteModel<HomeTimelineMongoEntity>> operations = new ArrayList<>();

    for (Map.Entry<UUID, ArrayList<UUID>> entry : map.entrySet()) {
      ArrayList<UUID> newFollows = entry.getValue();

      if (newFollows == null || newFollows.isEmpty()) {
        logger.warn("No follows found for " + entry.getKey() + ", unexpected behavior.");
        continue;
      }

      Bson filter = Filters.eq("_id", entry.getKey());
      Bson update =
          oper == HomeTimelineOperation.ADD
              ? Updates.addEachToSet("timelineIds", newFollows)
              : Updates.pullAll("timelineIds", newFollows);

      operations.add(new UpdateOneModel<>(filter, update));
    }

    mongoUtils.BulkWriteOperations(operations, repository.mongoCollection());
  }

  /**
   * Bulk updates users in the HomeTimeline Mongo collection, adding UUIDS to their timeline.
   *
   * @param followsMap A HashMap where each key corresponds to a user that needs to be updated, the
   *     value being the list of UUIDs to add.
   */
  public void HandleFollowsHomeTimeline(HashMap<UUID, ArrayList<UUID>> followsMap) {
    HandleOperationsHomeTimeline(followsMap, HomeTimelineOperation.ADD);
  }

  /**
   * Bulk updates users in the HomeTimeline Mongo collection, removing UUIDS from their timeline.
   *
   * @param unfollowsMap A HashMap where each key corresponds to a user that needs to be updated,
   *     the value being the list of UUIDs to remove.
   */
  public void HandleUnfollowsHomeTimeline(HashMap<UUID, ArrayList<UUID>> unfollowsMap) {
    HandleOperationsHomeTimeline(unfollowsMap, HomeTimelineOperation.DELETE);
  }
}
