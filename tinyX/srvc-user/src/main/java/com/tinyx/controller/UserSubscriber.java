package com.tinyx.controller;

import com.tinyx.Operation;
import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.UserService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Startup
@ApplicationScoped
public class UserSubscriber extends RedisStreamReader<UserRelationsQuery> {
  @Inject UserService userService;

  public UserSubscriber() {
    super();
  }

  /**
   * Processes a list of user relations queries (block and unblock only).
   *
   * @param data The list of user relations queries to process.
   */
  @Override
  public void process(List<UserRelationsQuery> data) {
    HashMap<UUID, ArrayList<UUID>> blocksMap = new HashMap<>();
    HashMap<UUID, ArrayList<UUID>> unblocksMap = new HashMap<>();

    for (UserRelationsQuery query : data) {
      if (query.operation == UserRelationsQuery.Operation.UNFOLLOW
          || query.operation == UserRelationsQuery.Operation.FOLLOW) continue;

      var map = query.operation == UserRelationsQuery.Operation.BLOCK ? blocksMap : unblocksMap;

      map.computeIfAbsent(query.srcUserId, k -> new ArrayList<>());
      map.get(query.srcUserId).add(query.targetUserId);
    }

    if (!blocksMap.isEmpty())
      userService.handleUserMongoWriteOperation(blocksMap, Operation.ADD, "blockedUsers");
    if (!unblocksMap.isEmpty())
      userService.handleUserMongoWriteOperation(unblocksMap, Operation.DELETE, "blockedUsers");
  }

  @Inject
  public UserSubscriber(final ReactiveRedisDataSource ds) {
    // The group is the service/repo name. It will be useful when there will be multiple k8s pods
    // for the
    // same service. For example if they are 3 repo-post running, messages will be balanced between
    // these 3 repo
    super(ds, UserRelationsQuery.class, "srvc-user", RedisChannel.SOCIAL);
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
