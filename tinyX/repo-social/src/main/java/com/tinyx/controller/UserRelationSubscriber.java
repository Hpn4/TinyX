package com.tinyx.controller;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.repository.entity.SocialRelationEntity;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Startup
@ApplicationScoped
public class UserRelationSubscriber extends RedisStreamReader<UserRelationsQuery> {
  @Inject SocialService service;

  public UserRelationSubscriber() {
    super();
  }

  @Inject
  public UserRelationSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, UserRelationsQuery.class, "repo-social", RedisChannel.SOCIAL);
  }

  @Override
  public void process(List<UserRelationsQuery> data) {
    List<SocialRelationEntity> blocks = new ArrayList<>();
    List<SocialRelationEntity> unblocks = new ArrayList<>();
    List<SocialRelationEntity> follows = new ArrayList<>();
    List<SocialRelationEntity> unfollows = new ArrayList<>();

    for (var i = 0; i < data.size(); i++) {
      UserRelationsQuery idata = data.get(i);
      SocialRelationEntity sre = new SocialRelationEntity(idata.srcUserId, idata.targetUserId);
      switch (data.get(i).operation) {
        case BLOCK -> {
          blocks.add(sre);
        }
        case FOLLOW -> {
          follows.add(sre);
        }
        case UNBLOCK -> {
          unblocks.add(sre);
        }
        case UNFOLLOW -> {
          unfollows.add(sre);
        }
        default -> {
          break;
        }
      }
    }

    service.createRelations(blocks, "BLOCK", "User", "User");
    service.createRelations(follows, "FOLLOW", "User", "User");
    service.deleteRelations(unblocks, "BLOCK", "User", "User");
    service.deleteRelations(unfollows, "FOLLOW", "User", "User");
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
