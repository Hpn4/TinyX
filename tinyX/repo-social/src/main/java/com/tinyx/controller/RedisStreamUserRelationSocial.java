package com.tinyx.controller;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import java.util.List;

public class RedisStreamUserRelationSocial extends RedisStreamReader<UserRelationsQuery> {
  @Inject SocialService service;

  public RedisStreamUserRelationSocial() {
    super();
  }

  public RedisStreamUserRelationSocial(final ReactiveRedisDataSource ds) {
    super(ds, UserRelationsQuery.class, "repo-social", RedisChannel.POST);
  }

  @Override
  public void process(List<UserRelationsQuery> data) {
    for (var i = 0; i < data.size(); i++) {
      switch (data.get(i).operation) {
        case BLOCK ->
            service.createRelation(
                data.get(i).srcUserId, data.get(i).targetUserId, "BLOCK", "User", "User");
        case FOLLOW ->
            service.createRelation(
                data.get(i).srcUserId, data.get(i).targetUserId, "FOLLOW", "User", "User");
        case UNBLOCK ->
            service.deleteRelation(
                data.get(i).srcUserId, data.get(i).targetUserId, "BLOCK", "User", "User");
        case UNFOLLOW ->
            service.deleteRelation(
                data.get(i).srcUserId, data.get(i).targetUserId, "FOLLOW", "User", "User");
        default -> {
          break;
        }
      }
    }
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
