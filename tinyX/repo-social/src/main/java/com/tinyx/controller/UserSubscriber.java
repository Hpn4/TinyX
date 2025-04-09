package com.tinyx.controller;

import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@Startup
@ApplicationScoped
public class UserSubscriber extends RedisStreamReader<UserQuery> {
  @Inject SocialService service;

  public UserSubscriber() {
    super();
  }

  @Inject
  public UserSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, UserQuery.class, "repo-social", RedisChannel.USER);
  }

  @Override
  public void process(List<UserQuery> data) {
    List<UUID> users =
        data.stream()
            .filter(q -> q.operation == UserQuery.Operation.CREATE)
            .map(q -> q.user.id)
            .toList();

    if (!users.isEmpty()) service.createUsers(users);
  }

  @Scheduled(every = "{tinyx.redis-stream.trim.every}")
  @Override
  public void trimStream() {
    super.trimStream();
  }

  @Scheduled(every = "{tinyx.redis-stream.claim.every}")
  @Override
  public void claimPendingMessages() {
    super.claimPendingMessages();
  }
}
