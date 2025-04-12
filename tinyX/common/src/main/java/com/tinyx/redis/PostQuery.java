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

  /** Constructs a new PostQuery object with the specified operation and post data. */
  public PostQuery(Operation operation, PostContract post) {
    this.operation = operation;
    this.post = post;
  }
}
