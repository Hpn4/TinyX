package com.tinyx.redis;

import java.util.UUID;

public class UserRelationsQuery {
  public enum Operation {
    FOLLOW,
    UNFOLLOW,
    BLOCK,
    UNBLOCK
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
