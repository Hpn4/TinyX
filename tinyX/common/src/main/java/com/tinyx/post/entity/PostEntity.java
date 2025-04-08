package com.tinyx.post.entity;

import com.tinyx.post.enumeration.PostType;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(collection = "Posts")
public class PostEntity {

  @BsonId public UUID id;
  public UUID userId;
  public String content;
  public ZonedDateTime creationDate;
  public UUID parentId;
  public UUID mediaId;
  public PostType postType;
  public List<UUID> children;

  public PostEntity() {}

  public PostEntity(
      UUID id,
      UUID userId,
      String content,
      ZonedDateTime creationDate,
      UUID parentId,
      UUID mediaId,
      PostType postType,
      List<UUID> children) {
    this.id = id;
    this.userId = userId;
    this.content = content;
    this.creationDate = creationDate;
    this.parentId = parentId;
    this.mediaId = mediaId;
    this.postType = postType;
    this.children = children;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PostEntity that = (PostEntity) o;
    if (!Objects.equals(id, that.id)) return false;
    if (!Objects.equals(children, that.children)) return false;
    if (!Objects.equals(userId, that.userId)) return false;
    if (!Objects.equals(content, that.content)) return false;
    if (!Objects.equals(parentId, that.parentId)) return false;
    if (!Objects.equals(mediaId, that.mediaId)) return false;
    return true;
  }
}
