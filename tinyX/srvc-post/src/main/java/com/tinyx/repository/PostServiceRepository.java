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
   * @return Optional<PostEntity>: The postEntity if found
   */
  public Optional<PostEntity> findPost(UUID postId) {
    return findByIdOptional(postId);
  }

  private List<PostEntity> find(Bson filter) {
    return mongoCollection().find(filter).into(new ArrayList<>());
  }

  public List<PostEntity> findPosts(List<UUID> postIds) {
    return find(Filters.in("_id", postIds));
  }

  public List<PostEntity> findPosts(List<UUID> postIds, List<UUID> blockedUser) {
    Bson filters =
        Filters.and(Filters.in("_id", postIds), Filters.not(Filters.in("userId", blockedUser)));

    return find(filters);
  }

  public List<PostEntity> findPostsReply(List<UUID> postIds, List<UUID> blockedUsers) {
    Bson filters =
        Filters.and(
            Filters.in("_id", postIds),
            Filters.eq("postType", PostType.REPLY),
            Filters.not(Filters.in("userId", blockedUsers)));

    return find(filters);
  }
}
