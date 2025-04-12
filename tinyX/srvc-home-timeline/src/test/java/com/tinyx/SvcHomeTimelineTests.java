package com.tinyx;

import static io.restassured.RestAssured.given;

import com.tinyx.mongo.MongoUtils;
import com.tinyx.redis.RedisUtils;
import com.tinyx.repository.SvcHomeTimelineRepository;
import com.tinyx.user.UserTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class SvcHomeTimelineTests {

  @Inject UserTestUtils userTestUtils;

  @Inject RedisUtils redisUtils;

  @Inject MongoUtils mongoUtils;

  @Inject SvcHomeTimelineRepository repository;

  @Test
  public void noUUID() {
    given().when().get("/timeline/home").then().statusCode(400);
  }

  @Test
  public void fakeUser() {
    given()
        .header("X-User", UUID.randomUUID().toString())
        .when()
        .get("/timeline/home")
        .then()
        .statusCode(404);
  }
}
