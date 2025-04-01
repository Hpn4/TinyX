package com.tinyx.repository;

import com.tinyx.post.entity.PostEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepositoryBase<PostEntity, UUID> {
  public PostRepository() {}

  /**
   * Create a new post
   *
   * @param posts post to be created
   */
  public void createPost(List<PostEntity> posts) {
    persist(posts);
  }

  /**
   * Delete a specific post
   *
   * @param ids ids of the posts to delete
   * @return the number of elements deleted
   */
  public long deletePost(List<UUID> ids) {
    return delete("id in ?1", ids);
  }

  // OPTIONAL: not specified in subject
  public void updatePost(List<PostEntity> posts) {
    update(posts);
  }
}
