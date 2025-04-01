package com.tinyx.redis.stream;

import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisPublisherFactory {

  private final RedisDataSource redisDataSource;

  public RedisPublisherFactory(RedisDataSource redisDataSource) {
    this.redisDataSource = redisDataSource;
  }

  public <T> RedisPublisher<T> createPublisher() {
    return new RedisPublisher<>(redisDataSource);
  }
}
