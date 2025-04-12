package com.tinyx.service;

import com.mongodb.client.model.*;
import com.tinyx.Operation;
import com.tinyx.home.entity.HomeTimelineMongoEntity;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.repository.RepoHomeTimelineRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
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
   * Bulk updates users in the HomeTimeline Mongo collection, adding UUIDS to their timeline.
   *
   * @param followsMap A HashMap where each key corresponds to a user that needs to be updated, the
   *     value being the list of UUIDs to add.
   */
  public void handleFollowsHomeTimeline(HashMap<UUID, ArrayList<UUID>> followsMap) {
    mongoUtils.handleMongoWriteOperationGeneric(
        followsMap, Operation.ADD, "timelineIds", repository.mongoCollection());
  }

  /**
   * Bulk updates users in the HomeTimeline Mongo collection, removing UUIDS from their timeline.
   *
   * @param unfollowsMap A HashMap where each key corresponds to a user that needs to be updated,
   *     the value being the list of UUIDs to remove.
   */
  public void handleUnfollowsHomeTimeline(HashMap<UUID, ArrayList<UUID>> unfollowsMap) {
    mongoUtils.handleMongoWriteOperationGeneric(
        unfollowsMap, Operation.DELETE, "timelineIds", repository.mongoCollection());
  }
}
