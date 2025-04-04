package com.tinyx;

import com.mongodb.client.MongoCollection;
import com.tinyx.home.entity.HomeTimelineMongoEntity;
import com.tinyx.mongo.MongoTestUtils;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.redis.RedisUtils;
import com.tinyx.redis.UserQuery;
import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.repository.RepoHomeTimelineRepository;
import com.tinyx.user.UserTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ejb.DuplicateKeyException;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.gradle.tooling.TestExecutionException;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
public class RepoHomeTimelineTests {

  private int REDIS_DELAY = 1000;

  @Inject Logger logger;

  @Inject RepoHomeTimelineRepository repository;

  @Inject RedisUtils redisUtils;
  @Inject UserTestUtils userTestUtils;
  @Inject MongoUtils mongoUtils;
  @Inject MongoTestUtils mongoTestUtils;

  MongoCollection<HomeTimelineMongoEntity> collection;

  @BeforeEach
  void SetUp() {
    this.collection = repository.mongoCollection();
  }

  /*
  @ParameterizedTest
  @ValueSource(ints = {1, 20, 100})
  public void testCreateMany(int n) throws InterruptedException, DuplicateKeyException {
    List<UserQuery> userCreateQueries = userTestUtils.randomUserCreationQueries(n);

    redisUtils.PostMany(RedisChannel.USER, userCreateQueries, UserQuery.class);

    // will throw and fail the test if any element is missing
    mongoTestUtils.<UUID>TestFind(
        "_id", userCreateQueries.stream().map(q -> q.user.id).toList(), true);
  }
  */

  @ParameterizedTest
  @ValueSource(ints = {2, 100})
  public void testCreateDuplicates(int n) throws InterruptedException, DuplicateKeyException {
    List<UserQuery> originalQueries = userTestUtils.randomUserCreationQueries(1);
    UserQuery original = originalQueries.get(0);

    ArrayList<UserQuery> duplicateQueries = new ArrayList<>();

    for (int i = 0; i < n; i++)
      duplicateQueries.add(new UserQuery(original.operation, original.user));

    redisUtils.PostMany(RedisChannel.USER, duplicateQueries, UserQuery.class);

    mongoTestUtils.TestFind(
        "_id", originalQueries.stream().map(q -> q.user.id).toList(), true, collection);
  }

  static Stream<Arguments> testAllProvider() {
    return Stream.of(Arguments.of(50, 2), Arguments.of(50, 100));
  }

  public void testAllPredicate(
      List<UserRelationsQuery> rQueries,
      BiPredicate<HomeTimelineMongoEntity, UserRelationsQuery> predicate) {
    List<HomeTimelineMongoEntity> found =
        mongoUtils.Find(
            "_id",
            rQueries.stream().map(rq -> rq.srcUserId).toList(),
            repository.mongoCollection());

    for (UserRelationsQuery rq : rQueries) {
      HomeTimelineMongoEntity foundUser =
          found.stream().filter(e -> e.userId.equals(rq.srcUserId)).findFirst().orElseThrow();

      if (!predicate.test(foundUser, rq))
        throw new TestExecutionException("Predicate could not be verified.");
    }
  }

  @ParameterizedTest
  @MethodSource("testAllProvider")
  public void testAll(int nbUsers, int nbRelations)
      throws InterruptedException, DuplicateKeyException {
    List<UserQuery> userQueries = userTestUtils.randomUserCreationQueries(nbUsers);
    redisUtils.PostMany(RedisChannel.USER, userQueries, UserQuery.class);

    mongoTestUtils.TestFind(
        "_id", userQueries.stream().map(q -> q.user.id).toList(), true, collection);

    logger.info("Creation passed.");

    List<UserRelationsQuery> rQueries =
        userTestUtils.randomRelationsQueriesBetweenUsers(
            userQueries, UserRelationsQuery.Operation.FOLLOW, nbRelations);
    redisUtils.PostMany(RedisChannel.SOCIAL, rQueries, UserRelationsQuery.class);
    Thread.sleep(REDIS_DELAY);

    testAllPredicate(rQueries, (e, rq) -> e.timelineIds.contains(rq.targetUserId));

    logger.info("Follows passed.");

    rQueries.forEach(fq -> fq.operation = UserRelationsQuery.Operation.UNFOLLOW);
    redisUtils.PostMany(RedisChannel.SOCIAL, rQueries, UserRelationsQuery.class);
    Thread.sleep(REDIS_DELAY);

    testAllPredicate(rQueries, (e, rq) -> !e.timelineIds.contains(rq.targetUserId));

    logger.info("Unfollows passed.");
  }
}
