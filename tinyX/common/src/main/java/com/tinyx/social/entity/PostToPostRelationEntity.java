package com.tinyx.social.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

public class PostToPostRelationEntity {
  public enum Kind {
    REPLY,
    REPOST
  }

  public Kind kind;

  public ZonedDateTime creationDate;

  public UUID srcPostId;

  public UUID targetPostId;

  public PostToPostRelationEntity(
      final Kind kind,
      final ZonedDateTime creationDate,
      final UUID srcPostId,
      final UUID targetPostId) {
    this.kind = kind;
    this.creationDate = creationDate;
    this.srcPostId = srcPostId;
    this.targetPostId = targetPostId;
  }
}
