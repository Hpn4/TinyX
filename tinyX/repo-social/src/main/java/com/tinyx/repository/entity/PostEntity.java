package com.tinyx.repository.entity;

import java.util.UUID;

/** Class that represent a post node in the neo4j database. */
public class PostEntity {
  public UUID id;

  public UUID authorId;

  public PostEntity() {}

  public PostEntity(UUID id, UUID authorId) {
    this.id = id;
    this.authorId = authorId;
  }
}
