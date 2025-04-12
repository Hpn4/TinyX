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

  /**
   * Constructs a new UserQuery with the specified operation and user data.
   *
   * @param operation The type of operation to be performed on the user.
   * @param user The user data to be included in the query.
   */
  public UserQuery(Operation operation, UserContract user) {
    this.operation = operation;
    this.user = user;
  }
}
