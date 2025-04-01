package com.tinyx.social.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

public class UserToPostRelationEntity {
  public enum Kind {
    POST,
    LIKE
  }

  public Kind kind;

  public ZonedDateTime creationDate;

  public UUID srcUserId;

  public UUID targetPostId;

  public UserToPostRelationEntity(
      final Kind kind,
      final ZonedDateTime creationDate,
      final UUID srcUserId,
      final UUID targetPostId) {
    this.kind = kind;
    this.creationDate = creationDate;
    this.srcUserId = srcUserId;
    this.targetPostId = targetPostId;
  }
}
