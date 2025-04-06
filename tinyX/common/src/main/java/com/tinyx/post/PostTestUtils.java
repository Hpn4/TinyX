package com.tinyx.post;

import com.mongodb.assertions.Assertions;
import com.mongodb.client.MongoCollection;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.post.contracts.PostContract;
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
  @Inject PostConverter postConverter;

  public String randomContent() {
    return "THIS IS A RANDOM CONTENT: " + UUID.randomUUID().toString().substring(0, 8);
  }

  public List<PostContract> randomPosts(int n) {
    ArrayList<PostContract> posts = new ArrayList<>();

    for (int i = 0; i < n; i++) {
      posts.add(
          new PostContract(
              UUID.randomUUID(), null, randomContent(), ZonedDateTime.now(), null, null));
    }

    return posts;
  }

  public List<PostQuery> randomPostQueries(int n) {
    List<PostContract> postsContracts = randomPosts(n);
    return postsContracts.stream().map(p -> new PostQuery(PostQuery.Operation.CREATE, p)).toList();
  }

  public List<PostQuery> postsToDeleteQueries(
      List<PostQuery> postQueries, int n_first_posts_to_delete) {
    return postQueries.stream()
        .limit(n_first_posts_to_delete)
        .map(p -> new PostQuery(PostQuery.Operation.DELETE, p.post))
        .toList();
  }

  public List<PostEntity> getPostsEntities(
      List<PostQuery> postsQueries, MongoCollection<PostEntity> collection) {
    List<PostEntity> postsEntities =
        mongoUtils.Find("_id", postsQueries.stream().map(p -> p.post.id).toList(), collection);
    return postsEntities;
  }

  public void assertPostsArePresent(List<PostQuery> expectedPosts, List<PostEntity> actualPosts) {
    List<PostEntity> expectedPostsEntities =
        expectedPosts.stream().map(p -> postConverter.convertPost(p.post)).toList();
    for (PostEntity p : expectedPostsEntities) {
      Assertions.assertTrue(actualPosts.stream().anyMatch(p::equals));
    }
  }

  public void assertDeletetionOfPostsSuccess(
      List<PostQuery> createdPosts, List<PostEntity> actualPosts) {
    for (int i = 0; i < 10; i++) {
      PostQuery pq = createdPosts.get(i);
      boolean contains = actualPosts.stream().anyMatch(p -> p.id.equals(pq.post.id));
      Assertions.assertFalse(contains);
    }
  }
}
