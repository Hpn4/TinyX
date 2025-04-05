package com.tinyx.redis;

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

  public UserRelationsQuery() {}

  public UserRelationsQuery(final Operation op, final UUID srcUserId, final UUID targetUserId) {
    this.operation = op;
    this.srcUserId = srcUserId;
    this.targetUserId = targetUserId;
  }
}
