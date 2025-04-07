package com.tinyx.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.post.entity.PostEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepositoryBase<PostEntity, UUID> {
  @Inject MongoUtils mongoUtils;

  public PostRepository() {}

  /**
   * Create a new post
   *
   * @param posts post to be created
   */
  public void createPost(List<PostEntity> posts) {
    mongoUtils.Insert(posts.stream(), this.mongoCollection());
  }

  /**
   * Delete a specific post
   *
   * @param ids ids of the posts to delete
   */
  public void deletePost(List<UUID> ids) {
    mongoUtils.Remove("_id", ids, this.mongoCollection());
  }

  public Optional<BulkWriteResult> ReplaceById(Stream<PostEntity> posts) {
    return mongoUtils.BulkWriteOperations(
        posts
            .map(
                p ->
                    (WriteModel<PostEntity>)
                        new ReplaceOneModel<PostEntity>(Filters.eq("_id", p.id), p))
            .toList(),
        this.mongoCollection());
  }

  /**
   * @param posts posts to delete by ID
   */
  public void updatePost(List<PostEntity> posts) {

    this.ReplaceById(posts.stream());
  }
}
