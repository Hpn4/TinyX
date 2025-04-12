package com.tinyx;

import com.tinyx.redis.RedisUtils;
import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.UserTimelineRepository;
import com.tinyx.user.UserTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CreateUsersTest {

  @Inject UserTimelineRepository userTimelineRepository;

  @Inject UserTestUtils userTestUtils;

  @Inject RedisUtils redisUtils;

  /** Test that duplicates users are properly handled (added once and no error) */
  @Test
  public void testDuplicateUsers() throws InterruptedException {
    List<UserQuery> userQueries = new ArrayList<>(userTestUtils.randomUserCreationQueries(3));
    userQueries.add(userQueries.get(0));
    userQueries.add(userQueries.get(2));
    userQueries.add(userQueries.get(2));

    redisUtils.postManyThenWait(RedisChannel.USER, userQueries, UserQuery.class);

    List<UUID> users = userQueries.stream().map(e -> e.user.id).distinct().toList();
    long res = userTimelineRepository.count("_id in ?1", users);

    Assertions.assertEquals(res, users.size());

    userTimelineRepository.deleteAll();
  }

  /** Just try to write a lot of users in one go and check if all of them were saved */
  @Test
  public void testCreateUsers() throws InterruptedException {
    List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(100);

    redisUtils.postManyThenWait(RedisChannel.USER, userQueries, UserQuery.class);

    List<UUID> users = userQueries.stream().map(e -> e.user.id).toList();
    long res = userTimelineRepository.count("_id in ?1", users);

    Assertions.assertEquals(res, users.size());

    userTimelineRepository.deleteAll();
  }

  /**
   * Try to write multiple times a lot of users and wait after only some push and check if all of
   * them were saved
   */
  @Test
  public void testCreateUsersTime() throws InterruptedException {
    List<UUID> finals = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(100);

      redisUtils.postMany(RedisChannel.USER, userQueries, UserQuery.class);

      // Wait after some data has been pushed
      if (i % 4 == 0) redisUtils.waitDelay();

      finals.addAll(userQueries.stream().map(e -> e.user.id).toList());
    }

    redisUtils.waitDelay();

    long res = userTimelineRepository.count("_id in ?1", finals);

    Assertions.assertEquals(res, finals.size());

    userTimelineRepository.deleteAll();
  }
}
