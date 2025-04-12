package com.tinyx;

import com.mongodb.client.MongoCollection;
import com.tinyx.post.PostTestUtils;
import com.tinyx.post.entity.PostEntity;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.RedisUtils;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.PostRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.*;

@QuarkusTest
public class RepoPostRedisTests {

  @Inject PostTestUtils postTestUtils;

  @Inject RedisUtils redisUtils;

  @Inject PostRepository postRepository;

  private int REDIS_DELAY = 1000;
  private int N_FIRST_POSTS_TO_DELETE = 10;

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class PostRedisTests {

    private MongoCollection<PostEntity> collection;
    private List<PostQuery> postsQueries;

    @BeforeEach
    void SetUp() {
      this.collection = postRepository.mongoCollection();
      postsQueries = postTestUtils.randomPostQueries(20);
    }

    @Test
    public void createMultiplePosts() throws InterruptedException {

      redisUtils.postMany(RedisChannel.POST, postsQueries, PostQuery.class);
      Thread.sleep(REDIS_DELAY);

      List<PostEntity> postsEntityResults =
          postTestUtils.getPostsEntities(postsQueries, collection);

      postTestUtils.assertPostsArePresent(postsQueries, postsEntityResults);
    }

    @Test
    public void deleteMultiplePosts() throws InterruptedException {

      redisUtils.postMany(RedisChannel.POST, postsQueries, PostQuery.class);
      Thread.sleep(REDIS_DELAY);

      List<PostQuery> postsToDeleteQueries =
          postTestUtils.postsToDeleteQueries(postsQueries, N_FIRST_POSTS_TO_DELETE);

      redisUtils.postMany(RedisChannel.POST, postsToDeleteQueries, PostQuery.class);
      Thread.sleep(REDIS_DELAY);

      List<PostEntity> postsEntityResults =
          postTestUtils.getPostsEntities(postsQueries, collection);

      postTestUtils.assertDeletionOfPostsSuccess(postsQueries, postsEntityResults);
    }
  }
}
