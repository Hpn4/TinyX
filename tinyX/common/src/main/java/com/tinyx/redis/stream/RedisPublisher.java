package com.tinyx.redis.stream;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.redis.datasource.stream.StreamCommands;
import java.util.Map;

public class RedisPublisher<T> {

  public static final String STREAM_KEY = "data";

  private final RedisDataSource redisDataSource;

  /**
   * Constructs a new RedisPublisher instance with the given RedisDataSource.
   *
   * @param redisDataSource The Redis data source that manages the connection to the Redis server.
   */
  public RedisPublisher(RedisDataSource redisDataSource) {
    this.redisDataSource = redisDataSource;
  }

  public void publish(RedisChannel channel, T message, Class<T> messageClass) {
    PubSubCommands<T> publisher = redisDataSource.pubsub(messageClass);
    publisher.publish(channel.toString(), message);
  }

  /**
   * Publishes a message to a Redis stream on the specified channel.
   *
   * @param channel The Redis channel where the message will be published. This is the destination
   *     stream.
   * @param message The message to be published to the stream.
   * @param messageClass The class type of the message being published.
   */
  public void publishStream(RedisChannel channel, T message, Class<T> messageClass) {
    StreamCommands<String, String, T> stream = redisDataSource.stream(messageClass);

    stream.xadd(channel.toString(), Map.of(STREAM_KEY, message));
  }
}
