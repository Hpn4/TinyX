package com.tinyx;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.tinyx.media.entity.MediaEntity;
import com.tinyx.repository.MediaRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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
  @Inject MongoClient mongo;
  MongoDatabase db;
  GridFSBucket bucket;

  @Inject MediaRepository mediaRepository;

  public void initDb() {
    db = mongo.getDatabase("Tinyx");
    bucket = GridFSBuckets.create(db, "Media");
  }

  @Test
  public void testNoFile() {
    initDb();

    MediaEntity entity = mediaRepository.getMedia(UUID.randomUUID());

    assertNull(entity);
  }
}
