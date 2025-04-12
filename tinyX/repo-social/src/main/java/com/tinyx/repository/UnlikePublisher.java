package com.tinyx.repository;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class UnlikePublisher {

  @Inject RedisPublisherFactory redisPublisherFactory;

  /**
   * publish request to unlike posts
   *
   * @param userId id of the user that will unlike a post
   * @param postId id of the post that will be unlike by the user*
   */
  public void publish(UUID userId, UUID postId) {
    LikePostQuery likePostQuery =
        new LikePostQuery(LikePostQuery.Operation.UNLIKE, userId, postId, null);

    redisPublisherFactory
        .<LikePostQuery>createPublisher()
        .publishStream(RedisChannel.LIKE, likePostQuery, LikePostQuery.class);
  }
}
