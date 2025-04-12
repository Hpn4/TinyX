package com.tinyx.repository;

import com.mongodb.client.model.*;
import com.tinyx.mongo.MongoUtils;
import com.tinyx.post.entity.PostEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bson.conversions.Bson;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepositoryBase<PostEntity, UUID> {
  @Inject MongoUtils mongoUtils;

  public PostRepository() {}

  private List<WriteModel<PostEntity>> addOrRemoveFromParents(List<PostEntity> posts, boolean add) {
    List<WriteModel<PostEntity>> writeModels = new ArrayList<>();

    // Group children posts by their shared parents
    Map<UUID, List<PostEntity>> childrenByParent =
        posts.stream()
            .filter(p -> p.parentId != null)
            .collect(Collectors.groupingBy(p -> p.parentId));

    for (Map.Entry<UUID, List<PostEntity>> entry : childrenByParent.entrySet()) {
      List<UUID> children = entry.getValue().stream().map(e -> e.id).toList();

      // For each parent we add/remove the grouped children list
      Bson filter = Filters.eq("_id", entry.getKey());
      Bson update =
          add ? Updates.addEachToSet("children", children) : Updates.pullAll("children", children);

      writeModels.add(new UpdateOneModel<>(filter, update));
    }

    return writeModels;
  }

  /**
   * Create a new post
   *
   * @param posts post to be created
   */
  public void createPost(List<PostEntity> posts) {
    List<WriteModel<PostEntity>> writeModels = new ArrayList<>();

    writeModels.addAll(posts.stream().map(InsertOneModel::new).toList());
    writeModels.addAll(addOrRemoveFromParents(posts, true));

    if (writeModels.isEmpty()) return;

    mongoUtils.bulkWriteOperations(writeModels, mongoCollection());
  }

  /**
   * Delete a specific post
   *
   * @param ids ids of the posts to delete
   */
  public void deletePost(List<UUID> ids) {
    List<PostEntity> postsToDelete = list("_id in ?1", ids);
    List<WriteModel<PostEntity>> writeModels = new ArrayList<>();

    for (PostEntity postEntity : postsToDelete) {
      // Set the parentId of children to null
      Bson filter = Filters.in("_id", postEntity.children);
      Bson update = Updates.set("parentId", null);

      writeModels.add(new UpdateManyModel<>(filter, update));

      // Delete the posts
      writeModels.add(new DeleteOneModel<>(Filters.eq("_id", postEntity.id)));
    }

    writeModels.addAll(addOrRemoveFromParents(postsToDelete, false));

    if (writeModels.isEmpty()) return;

    mongoUtils.bulkWriteOperations(writeModels, mongoCollection());
  }
}
