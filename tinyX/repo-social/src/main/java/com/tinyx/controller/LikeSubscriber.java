package com.tinyx.controller;

import com.tinyx.redis.LikePostQuery;
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
public class LikeSubscriber extends RedisStreamReader<LikePostQuery> {
  @Inject SocialService service;

  public LikeSubscriber() {
    super();
  }

  @Inject
  public LikeSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, LikePostQuery.class, "repo-social", RedisChannel.LIKE);
  }

  @Override
  public void process(List<LikePostQuery> data) {
    List<SocialRelationEntity> likes = new ArrayList<>();
    List<SocialRelationEntity> unlikes = new ArrayList<>();

    for (LikePostQuery datum : data) {
      SocialRelationEntity sre = new SocialRelationEntity(datum.srcUserId, datum.targetPostId);

      if (datum.operation == LikePostQuery.Operation.LIKE) likes.add(sre);
      else unlikes.add(sre);
    }

    service.createRelations(likes, "LIKE", "User", "Post");
    service.deleteRelations(unlikes, "LIKE", "User", "Post");
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
