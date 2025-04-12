package com.tinyx.search;

import static io.restassured.RestAssured.*;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class SearchEndpointTest {

  @Test
  public void testBadUser() {
    given()
        .contentType("application/json")
        .header("X-User", "fefekfoekgoekge")
        .when()
        .get("/search/posts")
        .then()
        .statusCode(400);
  }

  @Test
  public void testNoQuery() {
    given()
        .contentType("application/json")
        .header("X-User", UUID.randomUUID())
        .when()
        .get("/search/posts")
        .then()
        .statusCode(400);
  }

  @Test
  public void testWhitespaces() {
    given()
        .contentType("application/json")
        .header("X-User", UUID.randomUUID())
        .when()
        .param("phrase", "                          ")
        .param("hashtags", "")
        .get("/search/posts")
        .then()
        .statusCode(400);
  }

  @Test
  public void testEmptyHashtags() {
    given()
        .contentType("application/json")
        .header("X-User", UUID.randomUUID())
        .when()
        .param("hashtags", List.of("   ", "", "     "))
        .get("/search/posts")
        .then()
        .statusCode(400);
  }

  @Test
  public void testWhitespaces2() {
    given()
        .contentType("application/json")
        .header("X-User", UUID.randomUUID())
        .when()
        .param("phrase", "                          ")
        .param("hashtags", List.of("   ", "", "     "))
        .get("/search/posts")
        .then()
        .statusCode(400);
  }

  @Test
  public void testValid() {
    given()
        .contentType("application/json")
        .header("X-User", UUID.randomUUID())
        .when()
        .param("phrase", "hey")
        .get("/search/posts")
        .then()
        .statusCode(503); // Since no service is running
  }

  @Test
  public void testValid2() {
    given()
        .contentType("application/json")
        .header("X-User", UUID.randomUUID())
        .when()
        .param("phrase", "22")
        .param("hashtags", List.of("outerwilds", "space", "game"))
        .get("/search/posts")
        .then()
        .statusCode(503); // Since no service is running
  }
}
