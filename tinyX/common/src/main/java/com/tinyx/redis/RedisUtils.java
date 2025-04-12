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

  /**
   * Publishes a single query to a specified Redis channel.
   *
   * @param <T> The type of the query being sent to the Redis channel.
   * @param channel The Redis channel where the query should be published.
   * @param query The query to be published.
   * @param c The class type of the query.
   */
  public <T> void postOne(RedisChannel channel, T query, Class<T> c) {
    redisPublisherFactory.<T>createPublisher().publishStream(channel, query, c);
  }

  /**
   * Publishes multiple queries to a specified Redis channel.
   *
   * @param <T> The type of the queries being sent to the Redis channel.
   * @param channel The Redis channel where the queries should be published.
   * @param queries The list of queries to be published.
   * @param c The class type of the query.
   */
  public <T> void postMany(RedisChannel channel, List<T> queries, Class<T> c) {
    queries.forEach(q -> postOne(channel, q, c));
  }

  /**
   * Publishes multiple queries to a specified Redis channel, then waits for a specified delay.
   *
   * @param <T> The type of the queries being sent to the Redis channel.
   * @param channel The Redis channel where the queries should be published.
   * @param queries The list of queries to be published.
   * @param c The class type of the query.
   */
  public <T> void postManyThenWait(RedisChannel channel, List<T> queries, Class<T> c)
      throws InterruptedException {
    postMany(channel, queries, c);
    waitDelay();
  }
}
