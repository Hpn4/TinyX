package com.tinyx.post.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDate;
import java.util.UUID;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(collection = "Posts")
public class PostEntity {

  @BsonId public UUID id;
  public UUID userId;
  public String content;
  public LocalDate creationDate;
  public UUID parentId;
  public UUID mediaId;
  public int likes;

  public enum postType {
    NONE,
    REPLY,
    REPOST
  }

  public PostEntity(
      UUID id, UUID userId, String content, LocalDate creationDate, UUID parentId, UUID mediaId) {
    this.id = id;
    this.userId = userId;
    this.content = content;
    this.creationDate = creationDate;
    this.parentId = parentId;
    this.mediaId = mediaId;
    this.likes = 0;
  }
}
