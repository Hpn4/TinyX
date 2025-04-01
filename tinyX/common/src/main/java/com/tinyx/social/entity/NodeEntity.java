package com.tinyx.social.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

public class NodeEntity {
  public enum Kind {
    ACCOUNT,
    POST
  }

  public Kind kind;

  public ZonedDateTime creationDate;

  public UUID nodeId; // Will be the same as the _id of entities stored inside MongoDB

  public NodeEntity(final Kind kind, final ZonedDateTime creationDate, final UUID nodeId) {
    this.kind = kind;
    this.creationDate = creationDate;
    this.nodeId = nodeId;
  }
}
