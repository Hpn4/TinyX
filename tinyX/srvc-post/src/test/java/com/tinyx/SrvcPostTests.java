package com.tinyx;

import static io.restassured.RestAssured.given;

import com.tinyx.controller.request.CreatePostRequest;
import com.tinyx.post.enumeration.PostType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Implements tests regarding the repo-post service. In the future when more tests are added, this
 * file may need to be split into individual endpoint testing files.
 *
 * <p>Please add tests here as you implement/fix/work on stuff.
 */
@QuarkusTest
public class SrvcPostTests {
  @Test
  public void WrongCreatePost1() {
    given()
        .contentType(ContentType.JSON)
        .header("X-User", UUID.randomUUID().toString())
        .body(new CreatePostRequest(null, null, null, PostType.NONE))
        .when()
        .post("/posts/new")
        .then()
        .statusCode(400);
  }

  @Test
  public void WrongCreatePost2() {
    given()
        .contentType(ContentType.JSON)
        .header("X-User", UUID.randomUUID().toString())
        .body(new CreatePostRequest("some content", null, null, PostType.REPLY))
        .when()
        .post("/posts/new")
        .then()
        .statusCode(400);
  }
}
