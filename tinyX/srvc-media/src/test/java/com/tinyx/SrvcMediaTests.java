package com.tinyx;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import com.tinyx.media.utils.MediaTestUtils;
import com.tinyx.repository.MediaRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Implements tests regarding the srvc-media service. It doesn't have many tests as it's quite
 * simple.
 */
@QuarkusTest
public class SrvcMediaTests {
  @Inject MediaRepository mediaRepository;
  @Inject MediaTestUtils utils;

  private UUID uploadMedia(byte[] data) {
    UUID id = UUID.randomUUID();
    this.mediaRepository.uploadMediaForTests(id, new ByteArrayInputStream(data));
    return id;
  }

  @Test
  public void testNoMedia() {
    try {
      UUID mediaId = UUID.randomUUID();
      given().when().get("/media/get/" + mediaId).then().statusCode(404);
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testSimpleMedia() {
    try {
      byte[] data = utils.genRandomBytes();
      UUID mediaId = uploadMedia(data);
      Thread.sleep(50);

      InputStream media =
          given()
              .when()
              .get("/media/get/" + mediaId)
              .then()
              .statusCode(200)
              .contentType("application/octet-stream")
              .extract()
              .body()
              .asInputStream();
      assertNotNull(media);
      assertTrue(Arrays.equals(media.readAllBytes(), data));
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testSimpleMediaExists() {
    try {
      byte[] data = utils.genRandomBytes();
      UUID mediaId = uploadMedia(data);
      Thread.sleep(50);

      given()
          .when()
          .get("/media/exists/" + mediaId)
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body(is("true"));
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testSimpleMediaNotExists() {
    try {
      given()
          .when()
          .get("/media/exists/" + UUID.randomUUID())
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body(is("false"));
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testMediaDelete() {
    try {
      byte[] data = utils.genRandomBytes();
      UUID mediaId = uploadMedia(data);
      Thread.sleep(50);

      InputStream media =
          given()
              .when()
              .get("/media/get/" + mediaId)
              .then()
              .statusCode(200)
              .contentType("application/octet-stream")
              .extract()
              .body()
              .asInputStream();
      assertNotNull(media);
      assertTrue(Arrays.equals(media.readAllBytes(), data));

      mediaRepository.removeMediaForTests(mediaId);

      given().when().get("/media/get/" + mediaId).then().statusCode(404);
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }

  @Test
  public void testMediaDeleteExists() {
    try {
      byte[] data = utils.genRandomBytes();
      UUID mediaId = uploadMedia(data);
      Thread.sleep(50);

      given()
          .when()
          .get("/media/exists/" + mediaId)
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body(is("true"));

      mediaRepository.removeMediaForTests(mediaId);

      given()
          .when()
          .get("/media/exists/" + mediaId)
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body(is("false"));
    } catch (Exception e) {
      fail("Test failed with exception: " + e.getMessage());
    }
  }
}
