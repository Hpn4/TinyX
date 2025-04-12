package com.tinyx.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.tinyx.media.entity.MediaEntity;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bson.BsonBinary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MediaRepository {
  @Inject MongoClient mongo;
  MongoDatabase db;
  GridFSBucket bucket;

  @ConfigProperty(name = "quarkus.mongodb.database")
  String dbName;

  @ConfigProperty(name = "tinyx.mongo.media.bucket")
  String bucketName;

  @Inject Logger logger;

  // Somehow doesn't work if placed in constructor
  @PostConstruct
  public void init() {
    db = mongo.getDatabase(dbName);
    bucket = GridFSBuckets.create(db, bucketName);
  }

  public MediaEntity getMedia(UUID mediaId) {
    try {
      GridFSDownloadStream downloadStream = bucket.openDownloadStream(new BsonBinary(mediaId));
      // Not including the search for postId here because it doesn't feel needed.
      // In this service, postId is only necessary for one endpoint,
      // which doesn't use this function.
      return new MediaEntity(mediaId, downloadStream); // bytes
    } catch (Exception ignored) {
      return null;
    }
  }

  public boolean exists(UUID mediaId) {
    GridFSFile file = bucket.find(Filters.eq("_id", mediaId)).first();
    return file != null;
  }

  // Only for test purposes
  public void uploadMediaForTests(UUID mediaId, InputStream media) {
    CompletableFuture.runAsync(
        () -> {
          bucket.uploadFromStream(
              new BsonBinary(mediaId),
              "",
              media); // No metadata because it is not needed for the service tests.
        });
  }

  // Only for test purposes
  public void removeMediaForTests(UUID mediaId) {
    GridFSFile file = bucket.find(Filters.eq("_id", mediaId)).first();
    if (file != null) bucket.delete(file.getId());
  }
}
