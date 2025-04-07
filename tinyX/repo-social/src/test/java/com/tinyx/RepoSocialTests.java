package com.tinyx;

import com.tinyx.post.PostTestUtils;
import com.tinyx.redis.RedisUtils;
import com.tinyx.repository.SocialRepository;
import com.tinyx.user.UserTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;

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

  @Inject SocialRepository socialRepository;

  private int REDIS_DELAY = 1000;

  private int N_FIRST_POST_TO_DELETE = 10;

  // public void testApp() {
  //  assertTrue(true, "This is a basic test with JUnit 5");
  // }

  private UUID uniquepost = UUID.randomUUID();

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class PostRedisTest {
    /* @Test
    public void createUniquePost() throws InterruptedException {
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
      List<PostQuery> postQueryList = Arrays.asList(pq, pq);

      redisUtils.PostMany(RedisChannel.POST, postQueryList, PostQuery.class);
      List<UUID> postEntities = socialRepository.getPosts(Arrays.asList(uniquepost));
      assertTrue(postEntities.size() == 1);
    }*/
  }
}
