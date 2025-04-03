package com.tinyx.redis;

import com.tinyx.user.contracts.UserContract;

public class UserQuery {
  public enum Operation {
    CREATE,
    UPDATE,
    // Potentially add delete here
  }

  public Operation operation;
  public UserContract user;

  public UserQuery() {}

  public UserQuery(Operation operation, UserContract user) {
    this.operation = operation;
    this.user = user;
  }
}
