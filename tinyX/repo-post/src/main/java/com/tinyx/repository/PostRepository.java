package com.tinyx.repository;

import com.tinyx.post.entity.PostEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepositoryBase<PostEntity, UUID> {
  public PostRepository() {}

  /**
   * Create a new post
   *
   * @param post post to be created
   */
  public void createPost(PostEntity post) {}

  /**
   * Delete a specific post
   *
   * @param id id of the post to delete
   * @return the id of the deleted post
   */
  public UUID deletePost(UUID id) {
    return null;
  }

  // OPTIONAL: not specified in subject
  public void updatePost(PostEntity post) {}
}
