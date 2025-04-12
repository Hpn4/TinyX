package com.tinyx.service;

import com.mongodb.client.model.*;
import com.tinyx.home.entity.HomeTimelineMongoEntity;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.repository.RepoHomeTimelineRepository;
import com.tinyx.timeline.HomeTimelineOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RepoHomeTimelineService {

  @Inject Logger logger;

  @Inject MongoUtils mongoUtils;

  @Inject RepoHomeTimelineRepository repository;

  /**
   * Adds one or more newly created users to the HomeTimeline Mongo collection, with an empty list
   * as timelineIds.
   *
   * @param users The users' UUIDs to add.
   */
  public void initializeUsersHomeTimelines(List<UUID> users) {
    mongoUtils.insert(
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
  public void handleOperationsHomeTimeline(
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

    mongoUtils.bulkWriteOperations(operations, repository.mongoCollection());
  }

  /**
   * Bulk updates users in the HomeTimeline Mongo collection, adding UUIDS to their timeline.
   *
   * @param followsMap A HashMap where each key corresponds to a user that needs to be updated, the
   *     value being the list of UUIDs to add.
   */
  public void handleFollowsHomeTimeline(HashMap<UUID, ArrayList<UUID>> followsMap) {
    handleOperationsHomeTimeline(followsMap, HomeTimelineOperation.ADD);
  }

  /**
   * Bulk updates users in the HomeTimeline Mongo collection, removing UUIDS from their timeline.
   *
   * @param unfollowsMap A HashMap where each key corresponds to a user that needs to be updated,
   *     the value being the list of UUIDs to remove.
   */
  public void handleUnfollowsHomeTimeline(HashMap<UUID, ArrayList<UUID>> unfollowsMap) {
    handleOperationsHomeTimeline(unfollowsMap, HomeTimelineOperation.DELETE);
  }
}
