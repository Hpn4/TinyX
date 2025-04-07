package com.tinyx.repository.entity;

import java.util.UUID;

public class PostEntity {
  public UUID id;

  public UUID authId;

  public PostEntity() {}

  public PostEntity(UUID id, UUID authId) {
    this.id = id;
    this.authId = authId;
  }
}
