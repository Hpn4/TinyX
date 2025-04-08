package com.tinyx.controller;

import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.repository.entity.PostEntity;
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
public class PostSubscriber extends RedisStreamReader<PostQuery> {
  @Inject SocialService service;

  public PostSubscriber() {
    super();
  }

  @Inject
  public PostSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, PostQuery.class, "repo-social", RedisChannel.POST);
  }

  @Override
  public void process(List<PostQuery> data) {
    List<PostEntity> creations = new ArrayList<>();
    List<PostEntity> deletions = new ArrayList<>();

    for (var i = 0; i < data.size(); i++) {
      PostEntity pe = new PostEntity(data.get(i).post.id, data.get(i).post.userId);
      if (data.get(i).operation == PostQuery.Operation.CREATE) creations.add(pe);
      else deletions.add(pe);
    }
    service.createPosts(creations);
    service.deletePosts(deletions);
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
