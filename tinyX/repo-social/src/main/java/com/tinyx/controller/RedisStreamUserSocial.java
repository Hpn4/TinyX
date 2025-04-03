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
import java.util.ArrayList;
import java.util.List;

@Startup
@ApplicationScoped
public class RedisStreamUserSocial extends RedisStreamReader<UserQuery> {
  @Inject SocialService service;

  public RedisStreamUserSocial() {
    super();
  }

  @Inject
  public RedisStreamUserSocial(final ReactiveRedisDataSource ds) {
    super(ds, UserQuery.class, "repo-social", RedisChannel.USER);
  }

  @Override
  public void process(List<UserQuery> data) {

    List<UserContract> creation = new ArrayList<>();

    for (var i = 0; i < data.size(); i++) {
      if (data.get(i).operation == UserQuery.Operation.CREATE) creation.add(data.get(i).user);
    }
    service.createUser(creation);
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
