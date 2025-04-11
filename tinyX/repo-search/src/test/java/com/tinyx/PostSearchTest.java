package com.tinyx;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.tinyx.post.PostTestUtils;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.RedisUtils;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.SearchTestRepository;
import com.tinyx.search.entity.SearchPostEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostSearchTest {

  @Inject RedisUtils redisUtils;

  @Inject PostTestUtils postTestUtils;

  @Inject SearchTestRepository searchTestRepository;
  @Inject
  ElasticsearchClient esClient;

  @BeforeAll
  void setupIndex() throws IOException {
    boolean exists = esClient.indices().exists(e -> e.index("posts")).value();

    if (!exists) {
      var request = new CreateIndexRequest.Builder()
              .index("posts")
              .build();

      esClient.indices().create(request);
    }
  }

  @Test
  public void createPosts() throws InterruptedException {
    searchTestRepository.deleteAllPosts();
    redisUtils.waitDelay();

    List<PostQuery> queries = postTestUtils.randomPostQueries(10);

    redisUtils.postManyThenWait(RedisChannel.POST, queries, PostQuery.class);
    redisUtils.waitDelay(); // Elastic seems to index async

    List<UUID> results = searchTestRepository.searchAllPosts().stream().sorted().toList();
    List<UUID> expected = queries.stream().map(q -> q.post.id).sorted().toList();

    Assertions.assertEquals(expected, results);

    searchTestRepository.deleteAllPosts();
    redisUtils.waitDelay();

    Assertions.assertEquals(0, searchTestRepository.searchAllPosts().size());
  }

  @Test
  public void createPostHashtag() throws InterruptedException {
    searchTestRepository.deleteAllPosts();
    redisUtils.waitDelay();

    PostQuery queries = postTestUtils.randomPostQueries(1).get(0);
    queries.post.content =
        "Hey a small post wyth hashtags inside #OuterWilds #min22 #VideoGame2025";

    redisUtils.postOne(RedisChannel.POST, queries, PostQuery.class);
    redisUtils.waitDelay(); // Elastic seems to index async

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
    redisUtils.waitDelay();

    Assertions.assertEquals(0, searchTestRepository.searchAllPosts().size());
  }

  @Test
  public void createDeletePosts() throws InterruptedException {
    searchTestRepository.deleteAllPosts();
    redisUtils.waitDelay();

    // Create 10 posts
    List<PostQuery> queries = postTestUtils.randomPostQueries(10);

    redisUtils.postManyThenWait(RedisChannel.POST, queries, PostQuery.class);
    redisUtils.waitDelay(); // Elastic seems to index async

    // Check if they were indexed
    List<UUID> expected = new ArrayList<>(queries.stream().map(q -> q.post.id).sorted().toList());
    List<UUID> results = searchTestRepository.searchAllPosts().stream().sorted().toList();

    Assertions.assertEquals(expected, results);

    // Remove some posts
    List<PostQuery> toDelete = postTestUtils.postsToDeleteQueries(queries, 5);

    redisUtils.postManyThenWait(RedisChannel.POST, toDelete, PostQuery.class);

    expected.removeAll(toDelete.stream().map(q -> q.post.id).sorted().toList());
    results = searchTestRepository.searchAllPosts().stream().sorted().toList();

    Assertions.assertEquals(expected, results);

    // Clear the DB
    searchTestRepository.deleteAllPosts();
    redisUtils.waitDelay();

    Assertions.assertEquals(0, searchTestRepository.searchAllPosts().size());
  }

  @Test
  public void deletePosts() throws InterruptedException {
    searchTestRepository.deleteAllPosts();
    redisUtils.waitDelay();

    // Create 10 posts
    List<PostQuery> queries =
        postTestUtils.postsToDeleteQueries(postTestUtils.randomPostQueries(10), 10);

    redisUtils.postManyThenWait(RedisChannel.POST, queries, PostQuery.class);
    redisUtils.waitDelay(); // Elastic seems to index async

    // Check if nothing
    Assertions.assertEquals(0, searchTestRepository.searchAllPosts().size());
  }
}
