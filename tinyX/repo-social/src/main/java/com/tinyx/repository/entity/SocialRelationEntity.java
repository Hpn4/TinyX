package com.tinyx.repository.entity;

import java.time.Instant;
import java.util.UUID;

public class SocialRelationEntity {
  public UUID srcId;
  public UUID targetId;

  public Instant timestamp;

  public SocialRelationEntity(UUID srcId, UUID targetId) {
    this.srcId = srcId;
    this.targetId = targetId;
    this.timestamp = Instant.now();
  }
}
