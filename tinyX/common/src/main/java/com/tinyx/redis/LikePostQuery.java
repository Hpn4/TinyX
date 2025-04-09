package com.tinyx.redis;

import java.time.ZonedDateTime;
import java.util.UUID;

public class LikePostQuery {
  public enum Operation {
    LIKE,
    UNLIKE
  }

  public Operation operation;

  public UUID srcUserId;

  public UUID targetPostId;

  public LikePostQuery() {}

  public ZonedDateTime creationDate;

  public LikePostQuery(
      final Operation op,
      final UUID srcUserId,
      final UUID targetPostId,
      final ZonedDateTime creationDate) {
    this.operation = op;
    this.srcUserId = srcUserId;
    this.targetPostId = targetPostId;
    this.creationDate = creationDate;
  }
}
