package com.tinyx;

import static io.restassured.RestAssured.given;

import com.mongodb.client.MongoCollection;
import com.tinyx.mongo.MongoTestUtils;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.redis.RedisUtils;
import com.tinyx.repository.UserRepository;
import com.tinyx.user.UserTestUtils;
import com.tinyx.user.entity.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SrvcUserTest {

  private int REDIS_DELAY = 1000;

  @Inject Logger logger;

  @Inject UserRepository repository;

  @Inject RedisUtils redisUtils;
  @Inject UserTestUtils userTestUtils;
  @Inject MongoUtils mongoUtils;
  @Inject MongoTestUtils mongoTestUtils;

  MongoCollection<UserEntity> collection;

  static List<String> users;

  @BeforeAll
  static void setupAll() {
    users = UserTestUtils.randomUserNames(20);
  }

  @Nested
  class UserServiceTests {

    @Test
    void addUsers() throws InterruptedException {
      users.forEach(
          username -> {
            given()
                .contentType("application/json")
                .when()
                .post("/user/create/" + username)
                .then()
                .statusCode(200);
          });
    }

    @org.junit.jupiter.api.Nested
    class AccessTest {
      @Test
      void testAddUser_Duplicate() {
        users.forEach(
            username -> {
              given()
                  .contentType("application/json")
                  .when()
                  .post("/user/create/" + username)
                  .then()
                  .statusCode(409);
            });
      }

      @Test
      void testGetUserByName_Success() {
        users.forEach(
            username -> {
              given()
                  .contentType("application/json")
                  .when()
                  .get("/user/get/username/" + username)
                  .then()
                  .statusCode(200);
            });
      }

      @Test
      void testGetUserByName_NotFound() {
        given()
            .contentType("application/json")
            .when()
            .get("/user/get/username/notfound")
            .then()
            .statusCode(404);
      }

      @Test
      void testGetUserByName_BadRequest() {
        given()
            .contentType("application/json")
            .when()
            .get("/user/get/username/")
            .then()
            .statusCode(404);
      }

      @Test
      void testGetUserById_BadRequest() {
        given().when().get("/user/get/id/").then().statusCode(400);
      }

      /*
      @Test
      void testGetUsersByIds() {
        given()
                .queryParam("usersId", "123e4567-e89b-12d3-a456-426614174000")
                .queryParam("usersId", "223e4567-e89b-12d3-a456-426614174000")
                .when().get("/user/get/id")
                .then().statusCode(200);
      }
      */
    }
  }
  /*
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
  }*/
}
