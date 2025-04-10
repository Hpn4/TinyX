package com.tinyx;


import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

import com.tinyx.post.PostTestUtils;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.*;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.RelationsRepository;
import com.tinyx.repository.SocialRepository;
import com.tinyx.user.UserTestUtils;
import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.entity.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import com.tinyx.redis.RedisUtils;


/**
 * Implements tests regarding the repo-post service. In the future when more tests are added, this
 * file may need to be split into individual endpoint testing files.
 *
 * <p>Please add tests here as you implement/fix/work on stuff.
 */
@QuarkusTest
public class RepoSocialTests {
  @Inject RedisUtils redisUtils;

  @Inject PostTestUtils postTestUtils;

  @Inject UserTestUtils userTestUtils;


  @Inject RelationsRepository relationsRepository;


  @Inject SocialRepository socialRepository;

  private int REDIS_DELAY = 1000;

  private int N_FIRST_POST_TO_DELETE = 10;

  // public void testApp() {
  //  assertTrue(true, "This is a basic test with JUnit 5");
  // }

  private UUID uniquepost = UUID.randomUUID();


  private UUID uniqueUser = UUID.randomUUID();

  @Test
  public void createUniquePostNoDuplicateAndDelete() throws InterruptedException {
    int n = 5;
    UserEntity ue = userTestUtils.randomUsers(1).get(0);
    PostContract pc =
        new PostContract(
            uniquepost,
            ue.id,
            "sup bro",
            ZonedDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID());
    PostQuery pq = new PostQuery(PostQuery.Operation.CREATE, pc);
    List<PostQuery> postQueryList = new ArrayList<>(n);
    for (var i = 0; i < n; i++) {
      postQueryList.add(pq);
    }

    List<UUID> postIds = new ArrayList<>();
    postIds.add(uniquepost);
    redisUtils.PostMany(RedisChannel.POST, postQueryList, PostQuery.class);

    assertTrue(socialRepository.isNodeThere(postIds, "Post") == 1);
    PostQuery deleteQuery = new PostQuery(PostQuery.Operation.DELETE, pc);
    redisUtils.PostOne(RedisChannel.POST, deleteQuery, PostQuery.class);
    assertTrue(socialRepository.isNodeThere(postIds, "Post") == 0);
  }

  @Test
  public void createMultiplePost() throws InterruptedException {
    int n = 5;
    List<UUID> idToCreate = new ArrayList<>(n);
    List<PostQuery> posts = new ArrayList<>(n);

    List<UserEntity> lue = userTestUtils.randomUsers(n);
    for (var i = 0; i < n; i++) {
      PostContract pc =
          new PostContract(
              UUID.randomUUID(),
              lue.get(i).id,
              postTestUtils.randomContent(),
              ZonedDateTime.now(),
              UUID.randomUUID(),
              UUID.randomUUID());
      idToCreate.add(pc.id);
      posts.add(new PostQuery(PostQuery.Operation.CREATE, pc));
    }
    redisUtils.PostMany(RedisChannel.POST, posts, PostQuery.class);
    assertTrue(socialRepository.isNodeThere(idToCreate, "Post") == idToCreate.size());
    List<PostQuery> postsToDelete = new ArrayList<>(n);
    for (var i = 0; i < n; i++) {
      PostQuery originalQuery = posts.get(i);
      PostContract pc =
          new PostContract(
              originalQuery.post.id,
              originalQuery.post.userId,
              originalQuery.post.content,
              ZonedDateTime.now(),
              null,
              null);
      postsToDelete.add(new PostQuery(PostQuery.Operation.DELETE, pc));
    }

    redisUtils.PostMany(RedisChannel.POST, postsToDelete, PostQuery.class);
    assertTrue(socialRepository.isNodeThere(idToCreate, "Post") == 0);
  }

  @Test
  public void createUniqueUserNoDuplicate() throws InterruptedException {
    int n = 5;
    UserContract uc = new UserContract(uniqueUser, "bro", ZonedDateTime.now());

    UserQuery pq = new UserQuery(UserQuery.Operation.CREATE, uc);
    List<UserQuery> userQueryList = new ArrayList<>(n);
    for (var i = 0; i < n; i++) {
      userQueryList.add(pq);
    }

    redisUtils.PostMany(RedisChannel.USER, userQueryList, UserQuery.class);
    List<UUID> idToCreate = new ArrayList<>();
    idToCreate.add(uniqueUser);
    assertTrue(socialRepository.isNodeThere(idToCreate, "User") == 1);
    UserQuery deleteQuery = new UserQuery(UserQuery.Operation.DELETE, uc);
    redisUtils.PostOne(RedisChannel.USER, deleteQuery, UserQuery.class);
    assertTrue(socialRepository.isNodeThere(idToCreate, "User") == 0);
  }

  @Test
  public void createMultipleUser() throws InterruptedException {
    int n = 5;
    List<UUID> idToCreate = new ArrayList<>(n);
    List<UserQuery> users = new ArrayList<>(n);
    for (var i = 0; i < n; i++) {
      UserContract uc =
          new UserContract(UUID.randomUUID(), userTestUtils.RandomUsername(), ZonedDateTime.now());
      UserQuery uq = new UserQuery(UserQuery.Operation.CREATE, uc);
      idToCreate.add(uc.id);
      users.add(uq);
    }
    redisUtils.PostMany(RedisChannel.USER, users, UserQuery.class);
    assertTrue(socialRepository.isNodeThere(idToCreate, "User") == idToCreate.size());
  }

  @Test
  public void BlockCreationAndDeletion() throws InterruptedException {
    List<UserQuery> users = new ArrayList<>(2);
    for (var i = 0; i < 2; i++) {
      UserContract uc =
          new UserContract(UUID.randomUUID(), userTestUtils.RandomUsername(), ZonedDateTime.now());
      UserQuery uq = new UserQuery(UserQuery.Operation.CREATE, uc);
      users.add(uq);
    }
    redisUtils.PostMany(RedisChannel.USER, users, UserQuery.class);

    UserRelationsQuery urq =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.BLOCK,
            users.get(0).user.id,
            users.get(1).user.id,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, urq, UserRelationsQuery.class);
    assertTrue(
        relationsRepository.isThereRelation(
            "BLOCK", users.get(0).user.id, users.get(1).user.id, "User"));

    UserRelationsQuery deleturq =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.UNBLOCK,
            users.get(0).user.id,
            users.get(1).user.id,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, deleturq, UserRelationsQuery.class);
    assertFalse(
        relationsRepository.isThereRelation(
            "BLOCK", users.get(0).user.id, users.get(1).user.id, "User"));
  }

  @Test
  public void BlockNonExistingUser() throws InterruptedException {
    UserContract userContract =
        new UserContract(UUID.randomUUID(), userTestUtils.RandomUsername(), ZonedDateTime.now());
    UserQuery userQuery = new UserQuery(UserQuery.Operation.CREATE, userContract);
    redisUtils.PostOne(RedisChannel.USER, userQuery, UserQuery.class);

    UUID randomUUID = UUID.randomUUID();
    UserRelationsQuery userRelationsQuery1 =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.BLOCK, userContract.id, randomUUID, ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, userRelationsQuery1, UserRelationsQuery.class);
    assertFalse(relationsRepository.isThereRelation("BLOCK", userContract.id, randomUUID, "User"));

    UserRelationsQuery userRelationsQuery2 =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.BLOCK, randomUUID, userContract.id, ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, userRelationsQuery2, UserRelationsQuery.class);
    assertFalse(relationsRepository.isThereRelation("BLOCK", randomUUID, userContract.id, "User"));
  }

  @Test
  public void FollowCreationAndDeletion() throws InterruptedException {
    List<UserQuery> users = new ArrayList<>(2);
    for (var i = 0; i < 2; i++) {
      UserContract uc =
          new UserContract(UUID.randomUUID(), userTestUtils.RandomUsername(), ZonedDateTime.now());
      UserQuery uq = new UserQuery(UserQuery.Operation.CREATE, uc);
      users.add(uq);
    }
    redisUtils.PostMany(RedisChannel.USER, users, UserQuery.class);

    UserRelationsQuery urq =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.FOLLOW,
            users.get(0).user.id,
            users.get(1).user.id,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, urq, UserRelationsQuery.class);
    assertTrue(
        relationsRepository.isThereRelation(
            "FOLLOW", users.get(0).user.id, users.get(1).user.id, "User"));

    UserRelationsQuery deleturq =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.UNFOLLOW,
            users.get(0).user.id,
            users.get(1).user.id,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, deleturq, UserRelationsQuery.class);
    assertFalse(
        relationsRepository.isThereRelation(
            "FOLLOW", users.get(0).user.id, users.get(1).user.id, "User"));
  }

  @Test
  public void FollowNonExistingUserOrBlock() throws InterruptedException {
    List<UserQuery> users = new ArrayList<>(2);
    for (var i = 0; i < 2; i++) {
      UserContract uc =
          new UserContract(UUID.randomUUID(), userTestUtils.RandomUsername(), ZonedDateTime.now());
      UserQuery uq = new UserQuery(UserQuery.Operation.CREATE, uc);
      users.add(uq);
    }

    redisUtils.PostMany(RedisChannel.USER, users, UserQuery.class);

    UUID randomUUID = UUID.randomUUID();
    UserRelationsQuery userRelationsQuery1 =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.FOLLOW,
            users.get(0).user.id,
            randomUUID,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, userRelationsQuery1, UserRelationsQuery.class);
    assertFalse(
        relationsRepository.isThereRelation("FOLLOW", users.get(0).user.id, randomUUID, "User"));

    UserRelationsQuery userRelationsQuery2 =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.FOLLOW,
            randomUUID,
            users.get(0).user.id,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, userRelationsQuery2, UserRelationsQuery.class);
    assertFalse(
        relationsRepository.isThereRelation("FOLLOW", randomUUID, users.get(0).user.id, "User"));

    UserRelationsQuery blockUserRelationsQuery =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.BLOCK,
            users.get(0).user.id,
            users.get(1).user.id,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, blockUserRelationsQuery, UserRelationsQuery.class);

    UserRelationsQuery followWhileBlocked =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.FOLLOW,
            users.get(0).user.id,
            users.get(1).user.id,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, followWhileBlocked, UserRelationsQuery.class);
    assertFalse(
        relationsRepository.isThereRelation(
            "FOLLOW", users.get(0).user.id, users.get(1).user.id, "User"));

    UserRelationsQuery followWhileBlocked2 =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.FOLLOW,
            users.get(1).user.id,
            users.get(0).user.id,
            ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, followWhileBlocked2, UserRelationsQuery.class);
    assertFalse(
        relationsRepository.isThereRelation(
            "FOLLOW", users.get(1).user.id, users.get(0).user.id, "User"));
  }

  @Test
  public void LikeCreationAndDeletion() throws InterruptedException {
    UserContract uc =
        new UserContract(UUID.randomUUID(), userTestUtils.RandomUsername(), ZonedDateTime.now());
    UserQuery uq = new UserQuery(UserQuery.Operation.CREATE, uc);
    redisUtils.PostOne(RedisChannel.USER, uq, UserQuery.class);

    PostContract pc =
        new PostContract(
            UUID.randomUUID(),
            uc.id,
            "like",
            ZonedDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID());
    PostQuery pq = new PostQuery(PostQuery.Operation.CREATE, pc);
    redisUtils.PostOne(RedisChannel.POST, pq, PostQuery.class);
    LikePostQuery lpq =
        new LikePostQuery(LikePostQuery.Operation.LIKE, uc.id, pc.id, ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.LIKE, lpq, LikePostQuery.class);

    assertTrue(relationsRepository.isThereRelation("LIKE", uc.id, pc.id, "Post"));

    LikePostQuery dislike =
        new LikePostQuery(LikePostQuery.Operation.UNLIKE, uc.id, pc.id, ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, dislike, LikePostQuery.class);
    assertFalse(relationsRepository.isThereRelation("LIKE", uc.id, pc.id, "Post"));
  }

  @Test
  public void LikeNonExistingUserOrPostOrBlock() throws InterruptedException {

    UserContract uc =
        new UserContract(UUID.randomUUID(), userTestUtils.RandomUsername(), ZonedDateTime.now());
    UserQuery uq = new UserQuery(UserQuery.Operation.CREATE, uc);
    redisUtils.PostOne(RedisChannel.USER, uq, UserQuery.class);

    UserContract uc2 =
        new UserContract(UUID.randomUUID(), userTestUtils.RandomUsername(), ZonedDateTime.now());
    UserQuery uq2 = new UserQuery(UserQuery.Operation.CREATE, uc2);
    redisUtils.PostOne(RedisChannel.USER, uq2, UserQuery.class);

    PostContract pc =
        new PostContract(
            UUID.randomUUID(),
            uc2.id,
            "like",
            ZonedDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID());
    PostQuery pq = new PostQuery(PostQuery.Operation.CREATE, pc);
    redisUtils.PostOne(RedisChannel.POST, pq, PostQuery.class);

    UUID randomUUID = UUID.randomUUID();
    LikePostQuery likePostQuery1 =
        new LikePostQuery(LikePostQuery.Operation.LIKE, randomUUID, pc.id, ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.LIKE, likePostQuery1, LikePostQuery.class);
    assertFalse(relationsRepository.isThereRelation("LIKE", randomUUID, pc.id, "Post"));

    LikePostQuery likePostQuery2 =
        new LikePostQuery(LikePostQuery.Operation.LIKE, uc.id, randomUUID, ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.LIKE, likePostQuery2, LikePostQuery.class);
    assertFalse(relationsRepository.isThereRelation("LIKE", uc.id, randomUUID, "Post"));

    UserRelationsQuery blockUserRelationsQuery =
        new UserRelationsQuery(
            UserRelationsQuery.Operation.BLOCK, uc.id, uc2.id, ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.SOCIAL, blockUserRelationsQuery, UserRelationsQuery.class);

    LikePostQuery likePostQueryWhileBlocked =
        new LikePostQuery(LikePostQuery.Operation.LIKE, uc.id, pc.id, ZonedDateTime.now());
    redisUtils.PostOne(RedisChannel.LIKE, likePostQueryWhileBlocked, LikePostQuery.class);
    assertFalse(relationsRepository.isThereRelation("LIKE", uc.id, pc.id, "Post"));

  }
}
