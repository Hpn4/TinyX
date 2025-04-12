package com.tinyx.controller;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.RepoHomeTimelineService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;

/** See {@link RedisStreamReader} for documentation. */
@Startup
@ApplicationScoped
public class RelationsSubscriber extends RedisStreamReader<UserRelationsQuery> {

  @Inject RepoHomeTimelineService service;

  public RelationsSubscriber() {
    super();
  }

  @Inject
  public RelationsSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, UserRelationsQuery.class, "repo-home-timeline", RedisChannel.SOCIAL);
  }

  @Override
  public void process(List<UserRelationsQuery> data) {
    /**
     * Splits the queries depending on the operation, only Follows or Unfollows may affect the
     * HomeTimeline, others are ignored. Each query operating one of those two will be added to a
     * newly-created HashMap, as such : followsMap contains as value all newly-following users of
     * the key user unfollowsMap contains as value all newly-unfollowing users of the key user
     */
    HashMap<UUID, ArrayList<UUID>> followsMap = new HashMap<>();
    HashMap<UUID, ArrayList<UUID>> unfollowsMap = new HashMap<>();

    for (UserRelationsQuery query : data) {
      if (query.operation == UserRelationsQuery.Operation.UNBLOCK
          || query.operation == UserRelationsQuery.Operation.BLOCK) continue;

      var map = query.operation == UserRelationsQuery.Operation.FOLLOW ? followsMap : unfollowsMap;

      map.computeIfAbsent(query.srcUserId, k -> new ArrayList<>());
      map.get(query.srcUserId).add(query.targetUserId);
    }

    if (!followsMap.isEmpty()) service.handleFollowsHomeTimeline(followsMap);
    if (!unfollowsMap.isEmpty()) service.handleUnfollowsHomeTimeline(unfollowsMap);
  }

  /* Mandatory stuff, timing might be put inside the application properties to be cleaner */
  @Scheduled(every = "{tinyx.redis-stream.trim.every}")
  @Override
  protected void trimStream() {
    super.trimStream();
  }

  @Scheduled(every = "{tinyx.redis-stream.claim.every}")
  @Override
  protected void claimPendingMessages() {
    super.claimPendingMessages();
  }
}
