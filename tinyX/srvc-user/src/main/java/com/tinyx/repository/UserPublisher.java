package com.tinyx.repository;

import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import com.tinyx.user.converter.UserEntityToUserContractConverter;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserPublisher {
  @Inject UserEntityToUserContractConverter userEntityToUserContractConverter;

  @Inject RedisPublisherFactory redisPublisherFactory;

  /**
   * Publishes a user query operation in Redis.
   *
   * @param user The user entity to be published.
   * @param op The operation to be performed on the user.
   */
  public void post(UserEntity user, UserQuery.Operation op) {
    UserQuery userQuery = new UserQuery();
    userQuery.user = userEntityToUserContractConverter.convert(user);
    userQuery.operation = op;
    redisPublisherFactory
        .<UserQuery>createPublisher()
        .publishStream(RedisChannel.USER, userQuery, UserQuery.class);
  }
}
