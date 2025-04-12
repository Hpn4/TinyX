package com.tinyx.repository.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

public class SocialRelationEntity {
  public UUID srcId;
  public UUID targetId;

  public ZonedDateTime timestamp;

  public SocialRelationEntity(UUID srcId, UUID targetId, ZonedDateTime timestamp) {
    this.srcId = srcId;
    this.targetId = targetId;
    this.timestamp = timestamp;
  }
}
