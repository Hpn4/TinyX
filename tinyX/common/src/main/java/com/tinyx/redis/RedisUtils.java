package com.tinyx.redis;

import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RedisUtils {

  @Inject RedisPublisherFactory redisPublisherFactory;

  public int REDIS_DELAY = 1000;

  public void waitDelay() throws InterruptedException {
    Thread.sleep(REDIS_DELAY);
  }

  public <T> void postOne(RedisChannel channel, T query, Class<T> c) {
    redisPublisherFactory.<T>createPublisher().publishStream(channel, query, c);
  }

  public <T> void postMany(RedisChannel channel, List<T> queries, Class<T> c) {
    queries.forEach(q -> postOne(channel, q, c));
  }

  public <T> void postManyThenWait(RedisChannel channel, List<T> queries, Class<T> c)
      throws InterruptedException {
    postMany(channel, queries, c);
    waitDelay();
  }
}
