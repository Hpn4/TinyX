package com.tinyx.repository.entity;

import java.util.UUID;

public class PostEntity {
  public UUID id;

  public UUID authorId;

  public PostEntity() {}

  public PostEntity(UUID id, UUID authorId) {
    this.id = id;
    this.authorId = authorId;
  }
}
