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
public class SocialRedisStreamUserRelation extends RedisStreamReader<UserRelationsQuery> {
  @Inject SocialService service;

  public SocialRedisStreamUserRelation() {
    super();
  }

  @Inject
  public SocialRedisStreamUserRelation(final ReactiveRedisDataSource ds) {
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
      switch (data.get(i).operation) {
        case BLOCK -> {
          blocks.add(new SocialRelationEntity(idata.srcUserId,idata.targetUserId));
        }
        case FOLLOW -> {
          follows.add(new SocialRelationEntity(idata.srcUserId,idata.targetUserId));
        }
        case UNBLOCK -> {
          unblocks.add(new SocialRelationEntity(idata.srcUserId,idata.targetUserId));
        }
        case UNFOLLOW -> {
          unfollows.add(new SocialRelationEntity(idata.srcUserId,idata.targetUserId));
        }
        default -> {
          break;
        }
      }
    }

    service.createRelation(blocks, "BLOCK", "User", "User");
    service.createRelation(follows, "FOLLOW", "User", "User");
    service.deleteRelation(unblocks, "BLOCK", "User", "User");
    service.deleteRelation(unfollows, "FOLLOW", "User", "User");
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
