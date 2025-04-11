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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

  static List<String> users = new ArrayList<>();
  static List<UUID> userIds = new ArrayList<>();

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
            UUID id =
                given()
                    .contentType("application/json")
                    .when()
                    .post("/user/create/" + username)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getUUID("id");

            userIds.add(id);
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
      void testGetUserById_Success() {
        for (int i = 0; i < userIds.size(); i++) {
          String userName =
              given()
                  .contentType("application/json")
                  .when()
                  .get("/user/get/id/" + userIds.get(i))
                  .then()
                  .statusCode(200)
                  .extract()
                  .jsonPath()
                  .getString("userName");
          Assertions.assertEquals(userName, users.get(i));
        }
      }

      @Test
      void testGetUserById_NotFound() {
        given()
            .contentType("application/json")
            .when()
            .get("/user/get/id/" + UUID.randomUUID())
            .then()
            .statusCode(404);
      }

      @Test
      void testGetUserById_BadRequest() {
        given().contentType("application/json").when().get("/user/get/id/").then().statusCode(400);
      }

      @Test
      void testGetUsersByIds_Success() {
        List<String> usernames =
            given()
                .queryParam("usersId", userIds)
                .when()
                .get("/user/get/id")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("userName", String.class);
        Assertions.assertEquals(
            users.stream().sorted().toList(), usernames.stream().sorted().toList());
      }

      @Test
      void testGetUsersByIdsDuplicate_Success() {
        List<String> usernames =
            given()
                .queryParam("usersId", userIds)
                .queryParam("usersId", userIds)
                .when()
                .get("/user/get/id")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("userName", String.class);
        Assertions.assertEquals(
            users.stream().sorted().toList(), usernames.stream().sorted().toList());
      }

      @Test
      void testGetUsersByIds_NotFound() {
        given()
            .contentType("application/json")
            .queryParam("usersId", List.of(UUID.randomUUID()))
            .when()
            .get("/user/get/id")
            .then()
            .statusCode(404);
      }

      @Test
      void testGetUsersByIds_BadRequest() {
        given().contentType("application/json").when().get("/user/get/id").then().statusCode(400);
      }
    }
  }
}
