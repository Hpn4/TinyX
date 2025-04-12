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

  /** Default constructor for UserTimelineEntity. */
  public UserTimelineEntity() {}

  /**
   * Constructs a new UserTimelineEntity with the specified ID and list of posts.
   *
   * @param id The unique identifier of the user timeline.
   * @param posts A list of UserTimelinePostEntry representing the posts in the user's timeline.
   */
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
