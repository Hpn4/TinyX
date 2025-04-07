package com.tinyx.repository;

import com.tinyx.post.entity.PostEntity;
import com.tinyx.post.enumeration.PostType;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

  public List<PostEntity> findPosts(List<UUID> postIds) {
    return list("_id in ?1", postIds);
  }

  public List<PostEntity> findPosts(List<UUID> postIds, List<UUID> blockedUser) {
    return list("_id in ?1 and userId not in ?2", postIds, blockedUser);
  }

  public List<PostEntity> findPostsReply(List<UUID> postIds, List<UUID> blockedUsers) {
    return list(
        "_id in ?1 and postType = ?2 and userId not in ?3", postIds, PostType.REPLY, blockedUsers);
  }
}
