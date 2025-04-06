package com.tinyx.repository.entity;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

public class SocialRelationEntity {
  public UUID srcId;
  public UUID targetId;

  public ZonedDateTime timestamp;

  public SocialRelationEntity(UUID srcId, UUID targetId) {
    this.srcId = srcId;
    this.targetId = targetId;
    this.timestamp = ZonedDateTime.now();
  }
}
