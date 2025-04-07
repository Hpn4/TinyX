package com.tinyx;

import static io.restassured.RestAssured.given;

import com.tinyx.home.entity.HomeTimelineMongoEntity;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.redis.RedisUtils;
import com.tinyx.repository.SvcHomeTimelineRepository;
import com.tinyx.user.UserTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;
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

  @Test
  public void unreachableClient() throws InterruptedException {
    HomeTimelineMongoEntity e = new HomeTimelineMongoEntity(UUID.randomUUID(), new ArrayList<>());

    mongoUtils.Insert(Stream.of(e), repository.mongoCollection());
    Thread.sleep(100);

    given().header("X-User", e.userId).when().get("/timeline/home").then().statusCode(500);
  }
}
