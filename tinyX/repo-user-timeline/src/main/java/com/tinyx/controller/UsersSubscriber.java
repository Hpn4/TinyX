package com.tinyx.controller;

import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.UserTimelineService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

/** See {@link RedisStreamReader} for documentation. */
@Startup
@ApplicationScoped
public class UsersSubscriber extends RedisStreamReader<UserQuery> {

  @Inject UserTimelineService service;

  public UsersSubscriber() {
    super();
  }

  @Inject
  public UsersSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, UserQuery.class, "repo-user-timeline", RedisChannel.USER);
  }

  @Override
  public void process(List<UserQuery> data) {
    // Keep only CREATE queries and extract the userId
    List<UUID> usersId =
        data.stream()
            .filter(q -> q.operation == UserQuery.Operation.CREATE)
            .map(u -> u.user.id)
            .toList();

    service.createUsers(usersId);
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
