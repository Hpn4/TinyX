package com.tinyx.redis.post;

public class UserQuery {
  public enum Operation {
    CREATE,
    UPDATE,
    // Potentially add delete here
  }

  public Operation operation;
  public User user;

  public UserQuery(Operation operation, User user) {
    this.operation = operation;
    this.user = user;
  }
}
