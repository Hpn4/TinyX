package com.tinyx;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import com.tinyx.media.utils.MediaTestUtils;
import com.tinyx.post.PostTestUtils;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.RedisUtils;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.MediaTestRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Implements tests regarding the repo-media service. */
@QuarkusTest
public class RepoMediaTests {
  @Inject MediaTestRepository mediaRepository;

  @Inject MediaTestUtils utils;
  @Inject PostTestUtils postUtils;
  @Inject RedisUtils redisUtils;

  public RepoMediaTests() {}

  private UUID uploadMedia(byte[] data) {
    return given()
        .body(new ByteArrayInputStream(data))
        .when()
        .post("/media/upload")
        .then()
        .extract()
        .body()
        .as(UUID.class);
  }

  private List<UUID> uploadMedias(List<byte[]> medias) {
    return medias.stream().map(this::uploadMedia).toList();
  }

  @Test
  public void testSimpleInsert() {
    try {
      byte[] data = utils.genRandomBytes();

      UUID mediaId = uploadMedia(data);
      Thread.sleep(50); // Give time for the data to be inserted in the database

      byte[] readData = mediaRepository.getMedia(mediaId);
      assertNotNull(readData, "Expected to retrieve not null data");
      assertArrayEquals(readData, data, "Expected retrieved data to be the same as reference data");
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testConcurrentInserts() {
    try {
      List<byte[]> mediaList = utils.genRandomBytesList(5);
      List<UUID> mediaIds = uploadMedias(mediaList);

      Thread.sleep(500);

      for (int i = 0; i < mediaList.size(); i++) {
        byte[] readData = mediaRepository.getMedia(mediaIds.get(i));
        assertNotNull(readData, "Expected to find a media");
        assertArrayEquals(
            readData, mediaList.get(i), "Expected retrieved data to be the same as reference data");
      }
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testSimpleCreatePost() {
    try {
      UUID mediaId = uploadMedia(utils.genRandomBytes());
      Thread.sleep(50);

      List<PostQuery> posts =
          postUtils.randomPostQueries(2); // Add 2 posts, one with the media and one without
      posts.get(1).post.mediaId = mediaId;
      redisUtils.postManyThenWait(RedisChannel.POST, posts, PostQuery.class);

      List<UUID> linkedPosts = mediaRepository.getPosts(mediaId);
      assertEquals(1, linkedPosts.size(), "Expected a post to be linked");
      assertEquals(
          linkedPosts.get(0),
          posts.get(1).post.id,
          "Expected the linked post Id to be " + posts.get(1).post.id);
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testManyCreatePost() {
    try {
      UUID mediaId = uploadMedia(utils.genRandomBytes());
      Thread.sleep(50);

      // Add 5 posts, all of them with the media Id
      List<PostQuery> posts =
          postUtils.randomPostQueries(5).stream().peek(q -> q.post.mediaId = mediaId).toList();
      redisUtils.postManyThenWait(RedisChannel.POST, posts, PostQuery.class);

      List<UUID> linkedPosts = mediaRepository.getPosts(mediaId);
      assertEquals(5, linkedPosts.size(), "Expected 5 posts to be linked");
      for (int i = 0; i < linkedPosts.size(); i++) {
        assertEquals(
            linkedPosts.get(i),
            posts.get(i).post.id,
            "Expected post " + posts.get(i).post.id + " to be linked");
      }
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testVariousCreatePost() {
    try {
      UUID mediaOne = uploadMedia(utils.genRandomBytes());
      UUID mediaTwo = uploadMedia(utils.genRandomBytes());
      Thread.sleep(100);

      // Add 4 posts, all of them with the media Id
      List<PostQuery> posts = postUtils.randomPostQueries(4);
      // posts[0] does not have a media and it's normal
      posts.get(1).post.mediaId = mediaOne; // posts[1] and posts[3] share mediaOne
      posts.get(2).post.mediaId = mediaTwo;
      posts.get(3).post.mediaId = mediaOne;
      redisUtils.postManyThenWait(RedisChannel.POST, posts, PostQuery.class);

      List<UUID> linkedPostsOne = mediaRepository.getPosts(mediaOne);
      assertEquals(2, linkedPostsOne.size(), "Expected mediaOne to be linked with 2 posts");
      assertTrue(
          linkedPostsOne.contains(posts.get(1).post.id),
          "Expected mediaOne to be linked with post 1");
      assertTrue(
          linkedPostsOne.contains(posts.get(3).post.id),
          "Expected mediaOne to be linked with post 3");

      List<UUID> linkedPostsTwo = mediaRepository.getPosts(mediaTwo);
      assertEquals(1, linkedPostsTwo.size(), "Expected exactly one post linked to mediaTwo");
      assertEquals(
          linkedPostsTwo.get(0),
          posts.get(2).post.id,
          "Expected mediaTwo to be linked with post 2");
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testSimpleDeletePost() {
    try {
      UUID mediaId = uploadMedia(utils.genRandomBytes());
      Thread.sleep(50);

      List<PostQuery> posts =
          postUtils.randomPostQueries(2); // Add 2 posts, one with the media and one without
      posts.get(1).post.mediaId = mediaId;
      redisUtils.postManyThenWait(RedisChannel.POST, posts, PostQuery.class);

      posts = posts.stream().peek(q -> q.operation = PostQuery.Operation.DELETE).toList();
      redisUtils.postManyThenWait(RedisChannel.POST, posts, PostQuery.class);

      assertNull(
          mediaRepository.getMedia(mediaId), "Expected media to be deleted when its post was");
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testManyDeletePost() {
    try {
      UUID mediaId = uploadMedia(utils.genRandomBytes());
      Thread.sleep(50);

      // Add 5 posts, all of them with the media Id
      List<PostQuery> posts =
          postUtils.randomPostQueries(5).stream().peek(q -> q.post.mediaId = mediaId).toList();
      redisUtils.postManyThenWait(RedisChannel.POST, posts, PostQuery.class);

      posts = posts.stream().peek(q -> q.operation = PostQuery.Operation.DELETE).toList();

      redisUtils.postOne(RedisChannel.POST, posts.get(0), PostQuery.class);
      Thread.sleep(100);
      assertNotNull(
          mediaRepository.getMedia(mediaId),
          "Expected media to still be there after one of its posts was deleted");

      redisUtils.postManyThenWait(RedisChannel.POST, posts, PostQuery.class);

      assertNull(
          mediaRepository.getMedia(mediaId),
          "Expected media to be deleted when all of its posts were");
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }
}
