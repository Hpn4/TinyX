package com.tinyx.controller;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    List<List<UUID>> blocks = new ArrayList<>();
    List<List<UUID>> unblocks = new ArrayList<>();
    List<List<UUID>> follows = new ArrayList<>();
    List<List<UUID>> unfollows = new ArrayList<>();

    for (var i = 0; i < data.size(); i++) {
      switch (data.get(i).operation) {
        case BLOCK -> {
          List<UUID> ids = new ArrayList<>(2);
          ids.add(data.get(i).srcUserId);
          ids.add(data.get(i).targetUserId);
          blocks.add(ids);
        }
        case FOLLOW -> {
          List<UUID> ids = new ArrayList<>(2);
          ids.add(data.get(i).srcUserId);
          ids.add(data.get(i).targetUserId);
          follows.add(ids);
        }
        case UNBLOCK -> {
          List<UUID> ids = new ArrayList<>(2);
          ids.add(data.get(i).srcUserId);
          ids.add(data.get(i).targetUserId);
          unblocks.add(ids);
        }
        case UNFOLLOW -> {
          List<UUID> ids = new ArrayList<>(2);
          ids.add(data.get(i).srcUserId);
          ids.add(data.get(i).targetUserId);
          unfollows.add(ids);
        }
        default -> {
          break;
        }
      }
    }
    service.createRelation(blocks, "BLOCK", "User", "User");
    service.createRelation(blocks, "FOLLOW", "User", "User");
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
