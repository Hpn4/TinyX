package com.tinyx.repository;

import com.mongodb.client.model.Filters;
import com.tinyx.post.entity.PostEntity;
import com.tinyx.post.enumeration.PostType;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bson.conversions.Bson;

@ApplicationScoped
public class PostServiceRepository implements PanacheMongoRepositoryBase<PostEntity, UUID> {

  /**
   * Check if a post exist with his uuid
   *
   * @param postId: UUID of the post
   * @return Boolean
   */
  public Boolean existPost(UUID postId) {
    return findByIdOptional(postId).isPresent();
  }

  /**
   * Find post with his UUID
   *
   * @param postId: UUID of the post
   * @return The postEntity (if found).
   */
  public Optional<PostEntity> findPost(UUID postId) {
    return findByIdOptional(postId);
  }

  /**
   * Finds posts in the collection that match the given filter.
   *
   * @param filter The filter criteria to apply to the query.
   * @return A list of posts that match the filter.
   */
  private List<PostEntity> find(Bson filter) {
    return mongoCollection().find(filter).into(new ArrayList<>());
  }

  /**
   * Finds posts by their UUIDs.
   *
   * @param postIds The list of post UUIDs to search for.
   * @return A list of posts that match the provided UUIDs.
   */
  public List<PostEntity> findPosts(List<UUID> postIds) {
    return find(Filters.in("_id", postIds));
  }

  /**
   * Finds posts by their UUIDs, excluding posts from blocked users.
   *
   * @param postIds The list of post UUIDs to search for.
   * @param blockedUser The list of user UUIDs, to exclude from the results.
   * @return A list of posts that match the provided UUIDs and are not from blocked users.
   */
  public List<PostEntity> findPosts(List<UUID> postIds, List<UUID> blockedUser) {
    Bson filters =
        Filters.and(Filters.in("_id", postIds), Filters.not(Filters.in("userId", blockedUser)));

    return find(filters);
  }

  /**
   * Finds reply posts by their UUIDs, excluding posts from blocked users.
   *
   * @param postIds The list of post UUIDs to search for.
   * @param blockedUsers The list of user UUIDs, to exclude from the results.
   * @return A list of reply posts that match the provided UUIDs and are not from blocked users.
   */
  public List<PostEntity> findPostsReply(List<UUID> postIds, List<UUID> blockedUsers) {
    Bson filters =
        Filters.and(
            Filters.in("_id", postIds),
            Filters.eq("postType", PostType.REPLY),
            Filters.not(Filters.in("userId", blockedUsers)));

    return find(filters);
  }
}
