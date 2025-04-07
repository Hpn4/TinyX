package com.tinyx;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.tinyx.repository.MediaRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Implements tests regarding the repo-post service. In the future when more tests are added, this
 * file may need to be split into individual endpoint testing files.
 *
 * <p>Please add tests here as you implement/fix/work on stuff.
 */
@QuarkusTest
public class RepoMediaTests {
  @Inject MongoClient mongo;
  MongoDatabase db;
  GridFSBucket bucket;

  @Inject MediaRepository mediaRepository;

  public RepoMediaTests() {}

  @BeforeEach
  public void initDb() {
    db = mongo.getDatabase("Tinyx");
    bucket = GridFSBuckets.create(db, "Media");
  }

  @Test
  public void testSimpleInsert() {
    byte[] data = "Hello World".getBytes(StandardCharsets.UTF_8);
    InputStream media = new ByteArrayInputStream(data);

    UUID mediaId =
        given().body(media).when().post("/media/upload").then().extract().body().as(UUID.class);

    byte[] readData = mediaRepository.getMedia(mediaId);
    assertTrue(readData != null && Arrays.equals(readData, data));
  }
}
