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

  public ZonedDateTime creationDate;

  public LikePostQuery() {}

  /**
   * Constructs a new LikePostQuery object with the specified parameters.
   *
   * @param op The operation to be performed. It defines whether the like operation is a CREATE or
   *     DELETE.
   * @param srcUserId The UUID of the user performing the like operation.
   * @param targetPostId The UUID of the post that is being liked or unliked.
   * @param creationDate The timestamp when the like operation was created.
   */
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
