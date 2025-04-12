package com.tinyx.repository.publisher;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.UUID;

@ApplicationScoped
public class LikePublisher {

  @Inject RedisPublisherFactory redisPublisherFactory;

  public void publish(
      LikePostQuery.Operation operation, UUID srcId, UUID destId, ZonedDateTime timestamp) {
    final LikePostQuery userRelationsQuery = new LikePostQuery(operation, srcId, destId, timestamp);

    redisPublisherFactory
        .<LikePostQuery>createPublisher()
        .publishStream(RedisChannel.LIKE, userRelationsQuery, LikePostQuery.class);
  }
}
