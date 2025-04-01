package com.tinyx.media.entity;

import com.mongodb.client.gridfs.GridFSBucket;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.UUID;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(collection = "Media")
public class Media {

  @BsonId public UUID id;
  public GridFSBucket media;

  public Media(UUID id, GridFSBucket media) {
    this.id = id;
    this.media = media;
  }
}
