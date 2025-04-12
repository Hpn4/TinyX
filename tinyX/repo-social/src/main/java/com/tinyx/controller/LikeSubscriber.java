package com.tinyx.controller;

import com.tinyx.redis.LikePostQuery;
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

/** Read from a redis stream of LikePostQuery */
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

  /**
   * Catches LikePost queries me to keep track of which user has liked which post, it can also
   * delete those relations.
   *
   * @param queries The LikePost queries to handle.
   */
  @Override
  public void process(List<LikePostQuery> queries) {
    List<LikePostQuery> likes = new ArrayList<>();
    List<LikePostQuery> unlikes = new ArrayList<>();

    for (LikePostQuery query : queries) {
      if (query.operation == LikePostQuery.Operation.LIKE) likes.add(query);
      else unlikes.add(query);
    }

    service.likeRelations(likes);
    service.unlikeRelations(unlikes);
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
