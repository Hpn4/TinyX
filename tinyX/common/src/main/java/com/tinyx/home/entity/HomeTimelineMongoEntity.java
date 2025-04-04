package com.tinyx.home.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.List;
import java.util.UUID;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(collection = "HomeTimeline")
public class HomeTimelineMongoEntity extends PanacheMongoEntityBase {
  @BsonId public UUID userId;
  public List<UUID> timelineIds;

  public HomeTimelineMongoEntity() {}

  public HomeTimelineMongoEntity(UUID userId, List<UUID> timelineIds) {
    this.userId = userId;
    this.timelineIds = timelineIds;
  }
}
