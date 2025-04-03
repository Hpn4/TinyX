package com.tinyx.controller;

import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.SocialService;
import com.tinyx.user.contracts.UserContract;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Startup
@ApplicationScoped
public class RedisStreamUserSocial extends RedisStreamReader<UserQuery> {
  @Inject SocialService service;

  Logger log = Logger.getLogger(RedisStreamUserSocial.class);

  public RedisStreamUserSocial() {
    super();
  }

  @Inject
  public RedisStreamUserSocial(final ReactiveRedisDataSource ds) {
    super(ds, UserQuery.class, "repo-social", RedisChannel.USER);
  }

  @Override
  public void process(List<UserQuery> data) {
    List<UUID> users = data.stream()
            .filter(q -> q.operation == UserQuery.Operation.CREATE)
            .map(q -> q.user.id)
            .toList();

    log.info("Received users: " + users);

    service.createUsers(users);
  }

  @Scheduled(every = "10m")
  @Override
  public void trimStream() {
    super.trimStream();
  }

  @Scheduled(every = "5s")
  @Override
  public void claimPendingMessages() {
    super.claimPendingMessages();
  }
}
