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

  /**
   * Constructs a new HomeTimelineMongoEntity with the given user ID and timeline IDs.
   *
   * @param userId The ID of the user for whom the timeline is created.
   * @param timelineIds The list of UUIDs representing the posts in the user's timeline.
   */
  public HomeTimelineMongoEntity(UUID userId, List<UUID> timelineIds) {
    this.userId = userId;
    this.timelineIds = timelineIds;
  }
}
