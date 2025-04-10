package com.tinyx.repository;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.tinyx.timeline.entity.UserTimelineEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class UserTimelineRepository
    implements PanacheMongoRepositoryBase<UserTimelineEntity, UUID> {

  @ConfigProperty(name = "tinyx.srvc-user-timeline.collection")
  String collectionName;

  /**
   * Retrieves and merges the post UUIDs from the specified users' timelines. The resulting list is
   * sorted by their timestamp (either by post creation date or by like date) in descending order.
   * The posts from all specified users are combined into a single list, representing their merged
   * timelines.
   *
   * @param userIds A list of user UUIDs whose posts are to be fetched and merged.
   * @return A list of UUIDs representing the ordered post IDs from the merged timelines of the
   *     specified users, sorted by post timestamp in descending order. May include duplicates
   */
  public List<UUID> findOrderedPostsByUsers(List<UUID> userIds) {
    var pipeline =
        List.of(
            Aggregates.match(Filters.in("_id", userIds)),
            Aggregates.project(Projections.excludeId()),
            Aggregates.unwind("$posts"),
            Aggregates.sort(Sorts.descending("posts.timestamp")),
            Aggregates.project(Projections.exclude("posts.timestamp")));

    return mongoDatabase()
        .getCollection(collectionName)
        .aggregate(pipeline)
        .map(doc -> doc.get("posts", Document.class).get("_id", UUID.class))
        .into(new ArrayList<>());
  }

  /**
   * Retrieves and sorts the posts for the specified user, combining authored and liked posts. The
   * posts are sorted by their timestamp (either post creation date or like date) in descending
   * order.
   *
   * @param userId The UUID of the user whose posts are to be retrieved and sorted.
   * @return A list of UUIDs representing the ordered post IDs from the specified user's timeline,
   *     sorted by post timestamp in descending order.
   */
  public List<UUID> findOrderedPostsForUser(UUID userId) {
    return findOrderedPostsByUsers(List.of(userId));
  }
}
