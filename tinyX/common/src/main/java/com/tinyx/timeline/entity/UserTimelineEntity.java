package com.tinyx.timeline.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(collection = "UserTimeline")
public class UserTimelineEntity {
  @BsonId public UUID id; // User ID

  public List<UserTimelinePostEntry> posts;

  public UserTimelineEntity() {}

  public UserTimelineEntity(UUID id, List<UserTimelinePostEntry> posts) {
    this.id = id;
    this.posts = posts;
  }

  public static class UserTimelinePostEntry {
    public UUID id;

    public ZonedDateTime timestamp;

    public UserTimelinePostEntry() {}

    public UserTimelinePostEntry(UUID id, ZonedDateTime timestamp) {
      this.id = id;
      this.timestamp = timestamp;
    }
  }
}
