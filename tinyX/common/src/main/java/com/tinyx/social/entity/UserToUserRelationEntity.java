package com.tinyx.social.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

public class UserToUserRelationEntity {
  public enum Kind {
    FOLLOW,
    BLOCK
  }

  public Kind kind;

  public ZonedDateTime creationDate;

  public UUID srcUserId;

  public UUID targetUserId;

  public UserToUserRelationEntity(
      final Kind kind,
      final ZonedDateTime creationDate,
      final UUID srcUserId,
      final UUID targetUserId) {
    this.kind = kind;
    this.creationDate = creationDate;
    this.srcUserId = srcUserId;
    this.targetUserId = targetUserId;
  }
}
