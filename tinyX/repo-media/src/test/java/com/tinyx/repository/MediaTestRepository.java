package com.tinyx.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.model.Filters;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.bson.BsonBinary;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MediaTestRepository {
  @Inject MongoClient mongo;
  MongoDatabase db;
  MongoCollection<Document> filesCollection;
  GridFSBucket bucket;

  @ConfigProperty(name = "quarkus.mongodb.database")
  String dbName;

  @ConfigProperty(name = "tinyx.mongo.media.bucket")
  String bucketName;

  String postIdsPath = "postIds";

  @PostConstruct
  public void init() {
    db = mongo.getDatabase(dbName);
    bucket = GridFSBuckets.create(db, bucketName);
    filesCollection = db.getCollection(bucketName + ".files");
  }

  // Only used for testing purposes
  public byte[] getMedia(UUID id) {
    try {
      GridFSDownloadStream downloadStream = bucket.openDownloadStream(new BsonBinary(id));
      int fileLength = (int) downloadStream.getGridFSFile().getLength();
      byte[] bytesToWriteTo = new byte[fileLength];
      downloadStream.read(bytesToWriteTo);
      return bytesToWriteTo;
    } catch (Exception e) {
      return null;
    }
  }

  // Also for testing purposes
  public List<UUID> getPosts(UUID id) {
    try {
      return bucket
          .find(Filters.eq("_id", id))
          .first()
          .getMetadata()
          .getList(postIdsPath, UUID.class);
    } catch (Exception e) {
      return null;
    }
  }
}
