package com.tinyx.post.contracts;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public class PostContract {
  public UUID id;
  public UUID userId;
  public String content;
  public ZonedDateTime creationDate;
  public UUID parentId;
  public UUID mediaId;
  public int likes;

  public enum postType {
    NONE,
    REPLY,
    REPOST
  }

  public PostContract() {}

  public PostContract(
      UUID id,
      UUID userId,
      String content,
      ZonedDateTime creationDate,
      UUID parentId,
      UUID mediaId) {
    this.id = id;
    this.userId = userId;
    this.content = content;
    this.creationDate = creationDate;
    this.parentId = parentId;
    this.mediaId = mediaId;
    this.likes = 0;
  }

  @Override
  public String toString() {

    return "PostContract{"
        + "id="
        + id
        + ", userId="
        + userId
        + ", content='"
        + content
        + '\''
        + ", creationDate="
        + creationDate
        + ", parentId="
        + parentId
        + ", mediaId="
        + mediaId
        + ", likes="
        + likes
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PostContract that = (PostContract) o;
    if (likes != that.likes) return false;
    if (!Objects.equals(id, that.id)) return false;
    if (!Objects.equals(userId, that.userId)) return false;
    if (!Objects.equals(content, that.content)) return false;
    if (!Objects.equals(parentId, that.parentId)) return false;
    if (!Objects.equals(mediaId, that.mediaId)) return false;
    return true;
  }
}
