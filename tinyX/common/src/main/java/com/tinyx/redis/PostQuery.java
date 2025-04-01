package com.tinyx.redis;

import com.tinyx.post.contracts.PostContract;

public class PostQuery {
  public enum Operation {
    CREATE,
    DELETE,
    UPDATE,
  }

  public Operation operation;
  public PostContract post;

  public PostQuery() {}

  public PostQuery(Operation operation, PostContract post) {
    this.operation = operation;
    this.post = post;
  }
}
