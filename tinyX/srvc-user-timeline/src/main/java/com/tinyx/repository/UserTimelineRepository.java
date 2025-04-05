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

  public List<UUID> findOrderedPostsForUser(UUID userId) {
    return findOrderedPostsByUsers(List.of(userId));
  }
}
