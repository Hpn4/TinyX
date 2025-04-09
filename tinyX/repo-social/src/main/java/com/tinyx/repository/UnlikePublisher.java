package com.tinyx.repository;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.inject.Inject;
import java.util.UUID;

public class UnlikePublisher {

  @Inject RedisPublisherFactory redisPublisherFactory;

  public void publish(UUID userId, UUID postId) {
    LikePostQuery likePostQuery =
        new LikePostQuery(LikePostQuery.Operation.UNLIKE, userId, postId, null);

    redisPublisherFactory
        .<LikePostQuery>createPublisher()
        .publishStream(RedisChannel.LIKE, likePostQuery, LikePostQuery.class);
  }
}
