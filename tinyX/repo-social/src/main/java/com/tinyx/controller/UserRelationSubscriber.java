package com.tinyx.controller;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
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
  public void process(List<UserRelationsQuery> queries) {
    List<UserRelationsQuery> blocks = new ArrayList<>();
    List<UserRelationsQuery> unblocks = new ArrayList<>();
    List<UserRelationsQuery> follows = new ArrayList<>();
    List<UserRelationsQuery> unfollows = new ArrayList<>();

    for (UserRelationsQuery query : queries) {
      switch (query.operation) {
        case BLOCK -> blocks.add(query);
        case FOLLOW -> follows.add(query);
        case UNBLOCK -> unblocks.add(query);
        case UNFOLLOW -> unfollows.add(query);
        default -> {}
      }
    }

    if (!blocks.isEmpty()) service.blockRelations(blocks);
    if (!follows.isEmpty()) service.followRelations(follows);
    if (!unblocks.isEmpty()) service.unblockRelations(unblocks);
    if (!unfollows.isEmpty()) service.unfollowRelations(unfollows);
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
