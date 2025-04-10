package com.tinyx;

import com.tinyx.post.PostTestUtils;
import com.tinyx.redis.*;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.SocialTestRepository;
import com.tinyx.user.UserTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TestStream {

  @Inject RedisUtils redisUtils;

  @Inject UserTestUtils userTestUtils;

  @Inject PostTestUtils postTestUtils;

  @Inject SocialTestRepository repo;

  @Inject Logger log;

  private List<UUID> createUsers(int count) throws InterruptedException {
    List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(count);
    redisUtils.PostManyThenWait(RedisChannel.USER, userQueries, UserQuery.class);

    List<UUID> expected = userQueries.stream().map(q -> q.user.id).sorted().toList();
    List<UUID> results = repo.getAllUsers();

    Assertions.assertEquals(expected, results);

    return expected;
  }

  @Test
  public void createUsers() throws InterruptedException {
    repo.deleteAllData();

    createUsers(10);

    repo.deleteAllData();
    Assertions.assertNull(repo.getAllUsers());
  }

  @Test
  public void createPosts() throws InterruptedException {
    repo.deleteAllData();

    List<PostQuery> postQueries = postTestUtils.randomPostQueries(10);
    redisUtils.PostManyThenWait(RedisChannel.POST, postQueries, PostQuery.class);

    List<UUID> expected = postQueries.stream().map(q -> q.post.id).sorted().toList();
    List<UUID> results = repo.getAllPosts();

    Assertions.assertEquals(expected, results);

    repo.deleteAllData();
    Assertions.assertNull(repo.getAllPosts());
  }

  @Test
  public void createFollow() throws InterruptedException {
    repo.deleteAllData();

    List<UUID> users = createUsers(2);

    // Create a like between the two users
    UserRelationsQuery followQuery =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.FOLLOW, users.get(0), users.get(1), ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, followQuery, UserRelationsQuery.class);
    redisUtils.WaitDelay();

    // Test that the like exists
    List<UUID> result = repo.getFollow(users.get(0)); // 0 -FOLLOW-> 1
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(users.get(1), result.get(0));

    result = repo.getFollowers(users.get(1)); // 0 -FOLLOW-> 1
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(users.get(0), result.get(0));

    repo.deleteAllData();
    Assertions.assertNull(repo.getAllPosts());
  }
}
