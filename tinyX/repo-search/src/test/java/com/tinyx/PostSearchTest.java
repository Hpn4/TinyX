package com.tinyx;

import com.tinyx.post.PostTestUtils;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.RedisUtils;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.SearchTestRepository;
import com.tinyx.search.entity.SearchPostEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PostSearchTest {

  @Inject RedisUtils redisUtils;

  @Inject PostTestUtils postTestUtils;

  @Inject SearchTestRepository searchTestRepository;

  @Test
  public void createPosts() throws InterruptedException {
    searchTestRepository.deleteAllPosts();
    redisUtils.WaitDelay();

    List<PostQuery> queries = postTestUtils.randomPostQueries(10);

    redisUtils.PostManyThenWait(RedisChannel.POST, queries, PostQuery.class);
    redisUtils.WaitDelay(); // Elastic seems to index async

    List<UUID> results = searchTestRepository.searchAllPosts().stream().sorted().toList();
    List<UUID> expected = queries.stream().map(q -> q.post.id).sorted().toList();

    Assertions.assertEquals(expected, results);

    searchTestRepository.deleteAllPosts();
    redisUtils.WaitDelay();

    Assertions.assertEquals(0, searchTestRepository.searchAllPosts().size());
  }

  @Test
  public void createPostHashtag() throws InterruptedException {
    searchTestRepository.deleteAllPosts();
    redisUtils.WaitDelay();

    PostQuery queries = postTestUtils.randomPostQueries(1).get(0);
    queries.post.content =
        "Hey a small post wyth hashtags inside #OuterWilds #min22 #VideoGame2025";

    redisUtils.PostOne(RedisChannel.POST, queries, PostQuery.class);
    redisUtils.WaitDelay(); // Elastic seems to index async

    // Check if it was indexed
    List<SearchPostEntity> entities = searchTestRepository.searchAllPostsEntity();
    Assertions.assertEquals(1, entities.size());

    // Check if it's the right one
    SearchPostEntity entity = entities.get(0);
    Assertions.assertEquals(queries.post.id, entity.postId);

    // Check the hashtags list
    List<String> expected = List.of("outerwilds", "min22", "videogame2025");
    Assertions.assertEquals(expected, entity.hashtags);

    searchTestRepository.deleteAllPosts();
    redisUtils.WaitDelay();

    Assertions.assertEquals(0, searchTestRepository.searchAllPosts().size());
  }

  @Test
  public void createDeletePosts() throws InterruptedException {
    searchTestRepository.deleteAllPosts();
    redisUtils.WaitDelay();

    // Create 10 posts
    List<PostQuery> queries = postTestUtils.randomPostQueries(10);

    redisUtils.PostManyThenWait(RedisChannel.POST, queries, PostQuery.class);
    redisUtils.WaitDelay(); // Elastic seems to index async

    // Check if they were indexed
    List<UUID> expected = new ArrayList<>(queries.stream().map(q -> q.post.id).sorted().toList());
    List<UUID> results = searchTestRepository.searchAllPosts().stream().sorted().toList();

    Assertions.assertEquals(expected, results);

    // Remove some posts
    List<PostQuery> toDelete = postTestUtils.postsToDeleteQueries(queries, 5);

    redisUtils.PostManyThenWait(RedisChannel.POST, toDelete, PostQuery.class);

    expected.removeAll(toDelete.stream().map(q -> q.post.id).sorted().toList());
    results = searchTestRepository.searchAllPosts().stream().sorted().toList();

    Assertions.assertEquals(expected, results);

    // Clear the DB
    searchTestRepository.deleteAllPosts();
    redisUtils.WaitDelay();

    Assertions.assertEquals(0, searchTestRepository.searchAllPosts().size());
  }

  @Test
  public void deletePosts() throws InterruptedException {
    searchTestRepository.deleteAllPosts();
    redisUtils.WaitDelay();

    // Create 10 posts
    List<PostQuery> queries =
        postTestUtils.postsToDeleteQueries(postTestUtils.randomPostQueries(10), 10);

    redisUtils.PostManyThenWait(RedisChannel.POST, queries, PostQuery.class);
    redisUtils.WaitDelay(); // Elastic seems to index async

    // Check if nothing
    Assertions.assertEquals(0, searchTestRepository.searchAllPosts().size());
  }
}
