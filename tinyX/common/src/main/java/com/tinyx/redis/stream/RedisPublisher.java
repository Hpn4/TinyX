package com.tinyx.redis.stream;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.redis.datasource.stream.StreamCommands;
import java.util.Map;

public class RedisPublisher<T> {

  public static final String STREAM_KEY = "data";

  private final RedisDataSource redisDataSource;

  public RedisPublisher(RedisDataSource redisDataSource) {
    this.redisDataSource = redisDataSource;
  }

  public void publish(RedisChannel channel, T message, Class<T> messageClass) {
    PubSubCommands<T> publisher = redisDataSource.pubsub(messageClass);
    publisher.publish(channel.toString(), message);
  }

  public void publishStream(RedisChannel channel, T message, Class<T> messageClass) {
    StreamCommands<String, String, T> stream = redisDataSource.stream(messageClass);

    stream.xadd(channel.toString(), Map.of(STREAM_KEY, message));
  }
}
