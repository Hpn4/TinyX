package com.tinyx.post;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostConverter {
  public PostEntity convertPost(PostContract contract) {
    PostEntity entity =
        new PostEntity(
            contract.id,
            contract.userId,
            contract.content,
            contract.creationDate,
            contract.parentId,
            contract.mediaId);
    entity.likes = contract.likes;
    return entity;
  }

  public PostContract convertPost(PostEntity entity) {
    PostContract contract =
        new PostContract(
            entity.id,
            entity.userId,
            entity.content,
            entity.creationDate,
            entity.parentId,
            entity.mediaId);
    contract.likes = entity.likes;
    return contract;
  }
}
