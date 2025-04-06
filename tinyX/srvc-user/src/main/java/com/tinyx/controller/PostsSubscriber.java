package com.tinyx.controller;

import com.tinyx.redis.PostQuery;
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
public class PostsSubscriber extends RedisStreamReader<PostQuery> {
  @Inject UserService userService;

  public PostsSubscriber() {
    super();
  }

  @Override
  public void process(List<PostQuery> data) {
    HashMap<UUID, ArrayList<UUID>> createMap = new HashMap<>();
    HashMap<UUID, ArrayList<UUID>> deleteMap = new HashMap<>();

    for (PostQuery query : data) {
      if (query.operation == PostQuery.Operation.UPDATE) continue;

      var map = query.operation == PostQuery.Operation.CREATE ? createMap : deleteMap;

      map.computeIfAbsent(query.post.userId, k -> new ArrayList<>());
      map.get(query.post.userId).add(query.post.id);
    }

    if (!createMap.isEmpty())
      userService.handleMongoWriteOperation(createMap, UserService.UserOperation.ADD, "posts");
    if (!deleteMap.isEmpty())
      userService.handleMongoWriteOperation(deleteMap, UserService.UserOperation.DELETE, "posts");
  }

  @Inject
  public PostsSubscriber(final ReactiveRedisDataSource ds) {
    // The group is the service/repo name. It will be useful when there will be multiple k8s pods
    // for the
    // same service. For example if they are 3 repo-post running, messages will be balanced between
    // these 3 repo
    super(ds, PostQuery.class, "srvc-user", RedisChannel.POST);
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
