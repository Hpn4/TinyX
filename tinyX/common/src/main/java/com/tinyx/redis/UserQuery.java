package com.tinyx.redis;

import com.tinyx.user.contracts.UserContract;

public class UserQuery {
  public enum Operation {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

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
  public UserContract user;

  public UserQuery() {}

  public UserQuery(Operation operation, UserContract user) {
    this.operation = operation;
    this.user = user;
  }
}
