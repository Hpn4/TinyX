package com.tinyx.repository.publisher;

import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostPublisher {
  @Inject RedisPublisherFactory redisPublisherFactory;

  public void publish(PostQuery postQuery) {
    redisPublisherFactory
        .<PostQuery>createPublisher()
        .publishStream(RedisChannel.POST, postQuery, PostQuery.class);
  }
}
