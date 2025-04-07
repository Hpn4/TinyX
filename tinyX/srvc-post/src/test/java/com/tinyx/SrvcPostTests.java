package com.tinyx;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

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
  public void CreatePost() {
    given()
        .contentType(ContentType.JSON)
        .header("X-User", UUID.randomUUID().toString())
        .body(new CreatePostRequest("Je suis trop populaire X2", null, null, PostType.NONE))
        .when()
        .post("/posts/new")
        .then()
        .statusCode(200);
  }

  @Test
  public void DeletePost() {
    fail("Pas implémenté");
  }

  @Test
  public void UpdatePost() {
    fail("Pas implémenté");
  }

  @Test
  public void GetPostById() {
    fail("Pas implémenté");
  }

  @Test
  public void GetAllPost() {
    fail("Pas implémenté");
  }

  @Test
  public void GetAllPostsFromUser() {
    fail("Pas implémenté");
  }

  @Test
  public void GetRepliesByPostId() {
    fail("Pas implémenté");
  }
}
