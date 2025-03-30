package com.tinyx.redis.post;

public class PostQuery {
  public enum Operation {
    CREATE,
    DELETE,
    UPDATE,
  }

  public Operation operation;
  public Post post;

  public PostQuery(Operation operation, Post post) {
    this.operation = operation;
    this.post = post;
  }
}
