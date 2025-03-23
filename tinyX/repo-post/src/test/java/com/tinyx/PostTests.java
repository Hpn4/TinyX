package com.tinyx;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Implements tests regarding the repo-post service.
 * In the future when more tests are added, this file may need to be split into
 * individual endpoint testing files.
 *
 * Please add tests here as you implement/fix/work on stuff.
 */
@QuarkusTest
public class PostTests {
    @Test
    public void simpleNewPost()
    {
        UUID userId = UUID.randomUUID();
        given().contentType(MediaType.APPLICATION_JSON)
                .header("X-User", userId).body("{ \"text\" : \"FIXME\" }")
                .when().post("/posts/new")
                .then()
                .statusCode(200).body("authorId", equalTo(userId.toString())); // Is this how it's going to work ?
    }

    @Test
    public void simpleQueryPost()
    {
        UUID postId = UUID.randomUUID();
        given().when().get("/posts/post/" + postId.toString())
                .then().statusCode(200).body("postId", equalTo(postId));
    }
}
