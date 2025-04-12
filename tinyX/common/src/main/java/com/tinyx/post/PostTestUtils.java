package com.tinyx.post;

import com.mongodb.assertions.Assertions;
import com.mongodb.client.MongoCollection;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.converter.PostContractToPostEntityConverter;
import com.tinyx.post.entity.PostEntity;
import com.tinyx.redis.PostQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PostTestUtils {

  @Inject MongoUtils mongoUtils;
  @Inject PostContractToPostEntityConverter postContractToPostEntityConverter;

  public String randomContent() {
    return "THIS IS A RANDOM CONTENT: " + UUID.randomUUID().toString().substring(0, 8);
  }

  /**
   * Generates a list of random post contracts.
   *
   * @param n The number of random posts to generate.
   * @return List of PostContract objects with random content, user IDs, and creation times.
   */
  public List<PostContract> randomPosts(int n) {
    ArrayList<PostContract> posts = new ArrayList<>();

    for (int i = 0; i < n; i++) {
      posts.add(
          new PostContract(
              UUID.randomUUID(),
              UUID.randomUUID(),
              randomContent(),
              ZonedDateTime.now(),
              null,
              null));
    }

    return posts;
  }

  /**
   * Generates a list of random post queries.
   *
   * @param n The number of random post queries to generate.
   * @return A list of PostQuery objects, each representing a create operation with random post
   *     data.
   */
  public List<PostQuery> randomPostQueries(int n) {
    List<PostContract> postsContracts = randomPosts(n);
    return postsContracts.stream().map(p -> new PostQuery(PostQuery.Operation.CREATE, p)).toList();
  }

  /**
   * Generates a list of post deletion queries from a given list of post queries.
   *
   * @param postQueries The list of PostQuery objects to generate deletion queries from.
   * @param n_first_posts_to_delete The number of posts to delete from the provided list.
   * @return A list of PostQuery objects representing delete operations for the specified posts.
   */
  public List<PostQuery> postsToDeleteQueries(
      List<PostQuery> postQueries, int n_first_posts_to_delete) {
    return postQueries.stream()
        .limit(n_first_posts_to_delete)
        .map(p -> new PostQuery(PostQuery.Operation.DELETE, p.post))
        .toList();
  }

  /**
   * Retrieves a list of PostEntity objects from the MongoDB collection based on the given post
   * queries.
   *
   * @param postsQueries The list of PostQuery objects containing the post information to query by.
   * @param collection The MongoDB collection from which the post entities will be fetched.
   * @return A list of PostEntity objects corresponding to the post IDs in the given post queries.
   */
  public List<PostEntity> getPostsEntities(
      List<PostQuery> postsQueries, MongoCollection<PostEntity> collection) {
    List<PostEntity> postsEntities =
        mongoUtils.find("_id", postsQueries.stream().map(p -> p.post.id).toList(), collection);
    return postsEntities;
  }

  /**
   * Asserts that all the expected posts are present in the actual list of posts.
   *
   * @param expectedPosts The list of PostQuery objects representing the expected posts.
   * @param actualPosts The list of PostEntity objects representing the actual posts.
   */
  public void assertPostsArePresent(List<PostQuery> expectedPosts, List<PostEntity> actualPosts) {
    List<PostContract> postContract = expectedPosts.stream().map(p -> p.post).toList();
    List<PostEntity> expectedPostsEntities =
        postContractToPostEntityConverter.convert(postContract);
    for (PostEntity p : expectedPostsEntities) {
      Assertions.assertTrue(actualPosts.stream().anyMatch(p::equals));
    }
  }

  /**
   * Asserts that the deletion of posts was successful by verifying that the posts are no longer
   * present in the actual list.
   *
   * @param createdPosts The list of PostQuery objects representing the posts that were created.
   * @param actualPosts The list of PostEntity objects representing the actual posts after deletion.
   */
  public void assertDeletionOfPostsSuccess(
      List<PostQuery> createdPosts, List<PostEntity> actualPosts) {
    for (int i = 0; i < 10; i++) {
      PostQuery pq = createdPosts.get(i);
      boolean contains = actualPosts.stream().anyMatch(p -> p.id.equals(pq.post.id));
      Assertions.assertFalse(contains);
    }
  }
}
