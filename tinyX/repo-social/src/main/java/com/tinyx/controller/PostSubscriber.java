package com.tinyx.controller;

import com.tinyx.redis.PostQuery;
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
  public void process(List<PostQuery> queries) {
    List<PostQuery> creations = new ArrayList<>();
    List<PostQuery> deletions = new ArrayList<>();

    for (PostQuery query : queries) {
      switch (query.operation) {
        case CREATE -> creations.add(query);
        case DELETE -> deletions.add(query);
        default -> {}
      }
    }

    if (!creations.isEmpty()) service.createPosts(creations);
    if (!deletions.isEmpty()) service.deletePosts(deletions);
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
