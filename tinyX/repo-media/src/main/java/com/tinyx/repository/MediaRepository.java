package com.tinyx.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.tinyx.media.entity.MediaEntity;
import com.tinyx.mongo.MongoUtils;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MediaRepository {
  @Inject MongoClient mongo;
  MongoDatabase db;
  MongoCollection<Document> filesCollection;
  GridFSBucket bucket;

  @Inject Logger logger;

  @Inject MongoUtils mongoUtils;

  @ConfigProperty(name = "quarkus.mongodb.database")
  String dbName;

  @ConfigProperty(name = "tinyx.mongo.media.bucket")
  String bucketName;

  String metadataPostIdsPath = "metadata.postIds";
  String postIdsPath = "postIds";

  // Somehow doesn't work if placed in constructor
  @PostConstruct
  public void init() {
    db = mongo.getDatabase(dbName);
    bucket = GridFSBuckets.create(db, bucketName);
    filesCollection = db.getCollection(bucketName + ".files");
  }

  private Bson idFilter(UUID id) {
    return Filters.eq("_id", id);
  }

  /**
   * Uploads a media to the database. The duplicate ID event is considered impossible due to the
   * negligible probability of generating two random UUIDs. This operation is done asynchronously to
   * save performance.
   *
   * @param media The media to upload. Is referenced by an ID and contains data as a Stream.
   */
  public void uploadMedia(MediaEntity media) {
    // These options add a 'postId' field as metadata.
    // This allows to store a backlink to the Post collection that references
    // which post this media refers to. It is needed for media deletion.
    GridFSUploadOptions options =
        new GridFSUploadOptions().metadata(new Document(postIdsPath, new ArrayList<UUID>()));

    // Creates a new media in the bucket.
    // This is ran asynchronously so that the endpoint can return immediately, while
    // the file is uploaded asynchronously.
    CompletableFuture.runAsync(
        () -> {
          bucket.uploadFromStream(new BsonBinary(media.id), "", media.data, options);
        });
  }

  /**
   * Assigns a post to a media in a batch. This does not create a media if it is missing.
   *
   * @param mediaUpdates A map of every post linked to a single media to add to the database. The
   *     keys are media Ids and the values are corresponding lists of post Ids to link to these
   *     medias.
   */
  public void setMediaPosts(Map<UUID, ArrayList<UUID>> mediaUpdates) {
    List<WriteModel<Document>> operations = new ArrayList<>();

    for (Map.Entry<UUID, ArrayList<UUID>> entry : mediaUpdates.entrySet()) {
      Bson filter = idFilter(entry.getKey());
      // Add this post in the list of referenced posts of this media.
      Bson update = Updates.addEachToSet(metadataPostIdsPath, entry.getValue());
      operations.add(new UpdateOneModel<>(filter, update));
    }

    // Updating the document
    mongoUtils.BulkWriteOperations(operations, filesCollection);
  }

  /**
   * Removes a post Id from the list of referenced post Ids of a media. If that media then has no
   * more post references, it is deleted. If the post Id isn't already referenced by the post,
   * nothing happens.
   *
   * @param mediaId The media on which to do the information.
   * @param postId The post reference to remove.
   */
  public void removePost(UUID mediaId, UUID postId) {
    // Removing the postId from the stored Ids
    filesCollection.updateOne(idFilter(mediaId), Updates.pull(metadataPostIdsPath, postId));

    // If no more posts reference this media, it can be deleted.
    GridFSFile file =
        bucket.find(Filters.and(idFilter(mediaId), Filters.size(metadataPostIdsPath, 0))).first();
    if (file != null) bucket.delete(file.getId());
  }
}
