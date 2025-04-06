package com.tinyx.controller;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.UserTimelineService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/** See {@link RedisStreamReader} for documentation. */
@Startup
@ApplicationScoped
public class BlockUsersSubscriber extends RedisStreamReader<UserRelationsQuery> {

  @Inject UserTimelineService service;

  public BlockUsersSubscriber() {
    super();
  }

  @Inject
  public BlockUsersSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, UserRelationsQuery.class, "repo-user-timeline", RedisChannel.SOCIAL);
  }

  @Override
  public void process(List<UserRelationsQuery> data) {
    // Keep only BLOCK queries
    List<UserRelationsQuery> blockedUsers =
        data.stream().filter(q -> q.operation == UserRelationsQuery.Operation.BLOCK).toList();

    service.processBlock(blockedUsers);
  }

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
