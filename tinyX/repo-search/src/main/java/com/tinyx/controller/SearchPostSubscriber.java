package com.tinyx.controller;

import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.SearchService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Read RedisStream for post queries */
@Startup
@ApplicationScoped
public class SearchPostSubscriber extends RedisStreamReader<PostQuery> {

  @Inject SearchService searchService;

  public SearchPostSubscriber() {
    super();
  }

  @Inject
  public SearchPostSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, PostQuery.class, "repo-search", RedisChannel.POST);
  }

  /**
   * This function catches post queries in order to index and delete post.
   *
   * @param queries The post queries to handle.
   */
  @Override
  public void process(final List<PostQuery> queries) {
    final List<PostQuery> createPosts = new ArrayList<>();
    final List<UUID> deletePosts = new ArrayList<>();

    for (final PostQuery query : queries) {
      switch (query.operation) {
        case CREATE -> createPosts.add(query);
        case DELETE -> deletePosts.add(query.post.id);
        default -> {}
      }
    }

    if (!createPosts.isEmpty()) searchService.indexPosts(createPosts);
    if (!deletePosts.isEmpty()) searchService.deletePosts(deletePosts);
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
