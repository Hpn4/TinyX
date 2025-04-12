package com.tinyx.repository.publisher;

import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostPublisher {
  @Inject RedisPublisherFactory redisPublisherFactory;

  /**
   * Publishes a PostQuery to the Redis stream for post-related actions.
   *
   * @param postQuery The post query to be published.
   */
  public void publish(PostQuery postQuery) {
    redisPublisherFactory
        .<PostQuery>createPublisher()
        .publishStream(RedisChannel.POST, postQuery, PostQuery.class);
  }
}
