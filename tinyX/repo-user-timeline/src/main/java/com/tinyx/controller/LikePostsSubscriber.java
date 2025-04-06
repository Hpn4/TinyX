package com.tinyx.controller;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.UserTimelineService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/** See {@link RedisStreamReader} for documentation. */
@Startup
@ApplicationScoped
public class LikePostsSubscriber extends RedisStreamReader<LikePostQuery> {

  @Inject UserTimelineService service;

  public LikePostsSubscriber() {
    super();
  }

  @Inject
  public LikePostsSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, LikePostQuery.class, "repo-user-timeline", RedisChannel.LIKE);
  }

  @Override
  public void process(List<LikePostQuery> data) {
    service.processLike(data);
  }

  @Scheduled(every = "{tinyx.redis-stream.trim.every}")
  @Override
  protected void trimStream() {
    super.trimStream();
  }

  @Scheduled(every = "{tinyx.redis-stream.claim.every}")
  @Override
  protected void claimPendingMessages() {
    super.claimPendingMessages();
  }
}
