package com.tinyx.repository.publisher;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.UUID;

@ApplicationScoped
public class SocialPublisher {

  @Inject RedisPublisherFactory redisPublisherFactory;

  public void publish(
      UserRelationsQuery.Operation operation, UUID userId, UUID postId, ZonedDateTime timestamp) {
    final UserRelationsQuery query = new UserRelationsQuery(operation, userId, postId, timestamp);

    redisPublisherFactory
        .<UserRelationsQuery>createPublisher()
        .publishStream(RedisChannel.SOCIAL, query, UserRelationsQuery.class);
  }
}
