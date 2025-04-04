package com.tinyx.redis;

import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RedisUtils {

  @Inject RedisPublisherFactory redisPublisherFactory;

  public <T> void PostOne(RedisChannel channel, T query, Class<T> c) {
    redisPublisherFactory.<T>createPublisher().publishStream(channel, query, c);
  }

  public <T> void PostMany(RedisChannel channel, List<T> queries, Class<T> c) {
    queries.forEach(q -> PostOne(channel, q, c));
  }
}
