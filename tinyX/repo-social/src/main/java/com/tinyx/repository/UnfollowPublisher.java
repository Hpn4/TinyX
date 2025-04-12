package com.tinyx.repository;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class UnfollowPublisher {

  @Inject RedisPublisherFactory redisPublisherFactory;

  /**
   * publish request to unfollow users
   *
   * @param srcUserId the user that unfollow
   * @param dstUserId the user that will be unfollow
   */
  public void publish(UUID srcUserId, UUID dstUserId) {
    UserRelationsQuery unfollowQuery =
        new UserRelationsQuery(UserRelationsQuery.Operation.UNFOLLOW, srcUserId, dstUserId, null);

    redisPublisherFactory
        .<UserRelationsQuery>createPublisher()
        .publishStream(RedisChannel.SOCIAL, unfollowQuery, UserRelationsQuery.class);
  }
}
