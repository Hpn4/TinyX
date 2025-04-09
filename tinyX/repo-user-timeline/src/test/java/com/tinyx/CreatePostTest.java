package com.tinyx;

import com.tinyx.post.PostTestUtils;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.*;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.UserTimelineRepository;
import com.tinyx.timeline.utils.TimelineTestUtils;
import com.tinyx.user.UserTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CreatePostTest {

  @Inject UserTimelineRepository userTimelineRepository;

  @Inject TimelineTestUtils timelineTestUtils;

  @Inject UserTestUtils userTestUtils;

  @Inject PostTestUtils postTestUtils;

  @Inject RedisUtils redisUtils;

  private void checkUsers(List<UserQuery> userQueries) {
    List<UUID> users = userQueries.stream().map(e -> e.user.id).toList();
    Assertions.assertEquals(users.size(), userTimelineRepository.count("_id in ?1", users));
  }

  private void checkTimeline(Map<UUID, List<UUID>> entries) {
    for (Map.Entry<UUID, List<UUID>> entry : entries.entrySet()) {
      var stored =
          userTimelineRepository.findById(entry.getKey()).posts.stream()
              .map(p -> p.id)
              .sorted()
              .toList();
      var expected = entry.getValue().stream().sorted().toList();
      Assertions.assertEquals(expected, stored);
    }
  }

  private List<UUID> createAndPublish(UserQuery author, int count) {
    List<PostQuery> posts =
        postTestUtils.randomPostQueries(count).stream()
            .peek(p -> p.post.userId = author.user.id)
            .toList();

    redisUtils.PostMany(RedisChannel.POST, posts, PostQuery.class);

    return posts.stream().map(e -> e.post.id).toList();
  }

  private List<UUID> createAndLike(UserQuery author, int count) {
    List<PostQuery> posts = postTestUtils.randomPostQueries(count);

    List<LikePostQuery> likes =
        posts.stream()
            .map(
                e ->
                    new LikePostQuery(
                        LikePostQuery.Operation.LIKE,
                        author.user.id,
                        e.post.id,
                        e.post.creationDate))
            .toList();

    redisUtils.PostMany(RedisChannel.LIKE, likes, LikePostQuery.class);

    return posts.stream().map(e -> e.post.id).toList();
  }

  /** Test if inserting posts when there is no user does nothing */
  @Test
  public void testPostNoUser() throws InterruptedException {
    List<PostQuery> posts = postTestUtils.randomPostQueries(10);

    redisUtils.PostManyThenWait(RedisChannel.POST, posts, PostQuery.class);

    Assertions.assertEquals(0, userTimelineRepository.findAll().count());

    userTimelineRepository.deleteAll();
  }

  /** Create users, posts for eah users and see if there are in the timeline */
  @Test
  public void testCreateUserAndPost() throws InterruptedException {
    List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(1);
    Map<UUID, List<UUID>> entries = new HashMap<>();

    for (UserQuery userQuery : userQueries) {
      redisUtils.PostOne(RedisChannel.USER, userQuery, UserQuery.class);

      redisUtils.WaitDelay();

      entries.put(userQuery.user.id, createAndPublish(userQuery, 10));
    }

    redisUtils.WaitDelay();

    checkTimeline(entries);

    userTimelineRepository.deleteAll();
  }

  /** Create random users, each like 10 posts see if there are in timelines */
  @Test
  public void testLike() throws InterruptedException {
    // Create users
    List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(10);
    redisUtils.PostManyThenWait(RedisChannel.USER, userQueries, UserQuery.class);

    // Check is they exists
    checkUsers(userQueries);

    Map<UUID, List<UUID>> timelines = new HashMap<>();

    // Each user liked POSTS
    for (UserQuery userQuery : userQueries) {
      List<UUID> postIds = createAndLike(userQuery, 10);

      timelines.put(userQuery.user.id, postIds);
    }

    redisUtils.WaitDelay();
    checkTimeline(timelines);

    userTimelineRepository.deleteAll();
  }

  /**
   * Create random users, each like 10 posts see if there are in timelines then UNLIKE one of them
   */
  @Test
  public void testUnlike() throws InterruptedException {
    // Create users
    List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(10);
    redisUtils.PostManyThenWait(RedisChannel.USER, userQueries, UserQuery.class);

    // Check is they exists
    checkUsers(userQueries);

    Map<UUID, List<UUID>> timelines = new HashMap<>();

    // Each user liked POSTS
    for (UserQuery userQuery : userQueries) {
      List<UUID> postIds = new ArrayList<>(createAndLike(userQuery, 10));

      timelines.put(userQuery.user.id, postIds);
    }

    // Check if LIKED
    redisUtils.WaitDelay();
    checkTimeline(timelines);

    // Remove some liked posts
    for (int i = 0; i < userQueries.size(); i++) {
      UUID user = userQueries.get(i).user.id;
      var userTimeline = timelines.get(user);
      UUID postToUnlike = userTimeline.get(i);

      // Remove from timeline
      userTimeline.remove(i);

      LikePostQuery likePostQuery =
          new LikePostQuery(LikePostQuery.Operation.UNLIKE, user, postToUnlike, null);
      redisUtils.PostOne(RedisChannel.LIKE, likePostQuery, LikePostQuery.class);
    }

    // Check if UNLIKE were removed
    redisUtils.WaitDelay();
    checkTimeline(timelines);

    userTimelineRepository.deleteAll();
  }

  /**
   * Create random users, each like 10 posts see if there are in timelines then UNLIKE one of them
   */
  @Test
  public void testDelete() throws InterruptedException {
    // Create users
    List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(10);
    redisUtils.PostManyThenWait(RedisChannel.USER, userQueries, UserQuery.class);

    // Check is they exists
    checkUsers(userQueries);

    Map<UUID, List<UUID>> timelines = new HashMap<>();

    // Each user liked POSTS
    for (UserQuery userQuery : userQueries) {
      List<UUID> postIds = new ArrayList<>(createAndLike(userQuery, 10));

      timelines.put(userQuery.user.id, postIds);
    }

    // Check if LIKED
    redisUtils.WaitDelay();
    checkTimeline(timelines);

    // Delete some liked posts
    for (int i = 0; i < userQueries.size(); i++) {
      UUID user = userQueries.get(i).user.id;
      var userTimeline = timelines.get(user);
      UUID postToDelete = userTimeline.get(i);

      // Remove from timeline
      userTimeline.remove(i);

      PostQuery postQuery = new PostQuery();
      postQuery.operation = PostQuery.Operation.DELETE;
      postQuery.post = new PostContract();
      postQuery.post.id = postToDelete;
      postQuery.post.userId = UUID.randomUUID();

      redisUtils.PostOne(RedisChannel.POST, postQuery, PostQuery.class);
    }

    // Check if DELETED posts were removed
    redisUtils.WaitDelay();
    checkTimeline(timelines);

    userTimelineRepository.deleteAll();
  }

  /** Create users, posts for eah users and see if there are in the timeline */
  @Test
  public void testLike_2() throws InterruptedException {
    // Create users
    List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(2);
    redisUtils.PostManyThenWait(RedisChannel.USER, userQueries, UserQuery.class);

    // Check is they exists
    checkUsers(userQueries);

    Map<UUID, List<UUID>> timelines = new HashMap<>();

    // First user post 10 posts
    UUID userA = userQueries.get(0).user.id;
    List<UUID> postIds = createAndPublish(userQueries.get(0), 10);
    timelines.put(userA, postIds);

    // Check if they were create
    redisUtils.WaitDelay();
    checkTimeline(timelines);

    // second user created some posts
    UUID userB = userQueries.get(1).user.id;
    List<UUID> timelineB = new ArrayList<>(createAndPublish(userQueries.get(1), 10));
    timelines.put(userB, timelineB);

    // Check if they were created
    redisUtils.WaitDelay();
    checkTimeline(timelines);

    // userB like some posts od userA
    List<UUID> likedPosts = new ArrayList<>();
    for (int i = 0; i < postIds.size() / 2; i++) {
      LikePostQuery likePostQuery =
          new LikePostQuery(LikePostQuery.Operation.LIKE, userB, postIds.get(i), null);

      redisUtils.PostOne(RedisChannel.LIKE, likePostQuery, LikePostQuery.class);

      likedPosts.add(postIds.get(i));
    }

    timelineB.addAll(likedPosts);

    // Check if LIKED
    redisUtils.WaitDelay();
    checkTimeline(timelines);

    userTimelineRepository.deleteAll();
  }
}
