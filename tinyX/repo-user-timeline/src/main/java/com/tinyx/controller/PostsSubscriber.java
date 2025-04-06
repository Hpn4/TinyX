package com.tinyx.controller;

import com.tinyx.redis.PostQuery;
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
public class PostsSubscriber extends RedisStreamReader<PostQuery> {

  @Inject UserTimelineService service;

  public PostsSubscriber() {
    super();
  }

  @Inject
  public PostsSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, PostQuery.class, "repo-user-timeline", RedisChannel.POST);
  }

  @Override
  public void process(List<PostQuery> data) {
    service.processPosts(data);
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
