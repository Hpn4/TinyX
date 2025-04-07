package com.tinyx.service;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.repository.PostRestClient;
import com.tinyx.repository.UserTimelineRepository;
import com.tinyx.timeline.entity.UserTimelineEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import java.time.ZoneId;
import java.util.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class UserTimelineService {

  @Inject UserTimelineRepository repository;

  @Inject @RestClient PostRestClient postRestClient;

  public void createUsers(List<UUID> userIds) {
    repository.saveUsers(userIds);
  }

  /**
   * Partition the PostQueries from Redis in two groups: - Creation one: will add a new entry in the
   * user timeline of the one who created the post (the timestamp is the creation date of the post).
   * - Deletion one: will remove the post from all user timeline. These operations are batched.
   *
   * @param data List of post queries.
   */
  public void processPosts(List<PostQuery> data) {
    Map<UUID, List<UserTimelineEntity.UserTimelinePostEntry>> toAdd = new HashMap<>();
    List<UUID> toRemove = new ArrayList<>();

    for (PostQuery q : data) {
      if (q.operation == PostQuery.Operation.CREATE) {
        // We create a new entry with the ID and the creation date of the post
        var entry = new UserTimelineEntity.UserTimelinePostEntry();
        entry.id = q.post.id;
        // TODO: time format adapt
        entry.timestamp = q.post.creationDate.atStartOfDay(ZoneId.systemDefault());

        toAdd.computeIfAbsent(q.post.userId, e -> new ArrayList<>()).add(entry);
      } else if (q.operation == PostQuery.Operation.DELETE) toRemove.add(q.post.id);
    }

    repository.removeFromAllUsers(toRemove);
    repository.addToUsers(toAdd);
  }

  /**
   * Take a list of BLOCK user relations and for each one of these removes entries of liked post of
   * a blocked user.
   *
   * @param data List of user relations queries, excepting to contain only BLOCK queries.
   */
  public void processBlock(List<UserRelationsQuery> data) {
    Map<UUID, List<UUID>> entriesToRemovePerUser = new HashMap<>();

    for (UserRelationsQuery q : data) {
      List<UUID> blockedUerPosts = new ArrayList<>();

      try {
        // Get posts of the blocked user
        blockedUerPosts =
            postRestClient.getUserPosts(q.targetUserId, q.targetUserId).stream()
                .map(p -> p.id)
                .toList();
      } catch (WebApplicationException e) {
        int status = e.getResponse().getStatus();

        // If there is another error than a user not found (previously deleted) we cancel the
        // processing
        if (status != 200 && status != 404) throw e;
      }

      // We then remove all posts of the blocked user (`targetUserId`) from the `srcUserId` timeline
      entriesToRemovePerUser
          .computeIfAbsent(q.srcUserId, e -> new ArrayList<>())
          .addAll(blockedUerPosts);
    }

    repository.removeForUsers(entriesToRemovePerUser);
  }

  /**
   * Partition a list of LIKE/UNLIKE queries in two groups: - LIKE: simply add a new entry (the
   * timestamp is when the like was done) - UNLIKE: simply remove the previously liked post These
   * operations are batched.
   *
   * @param data List of like queries.
   */
  public void processLike(List<LikePostQuery> data) {
    Map<UUID, List<UserTimelineEntity.UserTimelinePostEntry>> entriesToAddPerUser = new HashMap<>();
    Map<UUID, List<UUID>> entriesToRemovePerUser = new HashMap<>();

    for (LikePostQuery q : data) {
      if (q.targetPostId == null) continue;

      if (q.operation == LikePostQuery.Operation.LIKE) {
        // We create a new entry with the liked post and as timestamp, the date of the like
        var entry = new UserTimelineEntity.UserTimelinePostEntry();
        entry.id = q.targetPostId;
        entry.timestamp = q.creationDate;

        entriesToAddPerUser.computeIfAbsent(q.srcUserId, e -> new ArrayList<>()).add(entry);
      } else
        entriesToRemovePerUser
            .computeIfAbsent(q.srcUserId, e -> new ArrayList<>())
            .add(q.targetPostId);
    }

    repository.removeForUsers(entriesToRemovePerUser);
    repository.addToUsers(entriesToAddPerUser);
  }
}
