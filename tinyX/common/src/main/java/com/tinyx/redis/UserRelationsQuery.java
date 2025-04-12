package com.tinyx.redis;

import java.time.ZonedDateTime;
import java.util.UUID;

public class UserRelationsQuery {
  public enum Operation {
    FOLLOW("FOLLOW"),
    UNFOLLOW("UNFOLLOW"),
    BLOCK("BLOCK"),
    UNBLOCK("UNBLOCK");

    private final String operation;

    Operation(String operation) {
      this.operation = operation;
    }

    @Override
    public String toString() {
      return this.operation;
    }
  }

  public Operation operation;

  public UUID srcUserId;

  public UUID targetUserId;

  public ZonedDateTime creationDate;

  public UserRelationsQuery() {}

  /**
   * Constructs a new with the specified parameters.
   *
   * @param op The type of operation to be performed on the user relation.
   * @param srcUserId The ID of the source user initiating the relation.
   * @param targetUserId The ID of the target user with whom the relation is being created or
   *     modified.
   * @param creationDate The date and time when the relation was created or modified.
   */
  public UserRelationsQuery(
      final Operation op,
      final UUID srcUserId,
      final UUID targetUserId,
      final ZonedDateTime creationDate) {
    this.operation = op;
    this.srcUserId = srcUserId;
    this.targetUserId = targetUserId;
    this.creationDate = creationDate;
  }
}
