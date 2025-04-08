package com.tinyx.post.converter;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostEntityToPostContractConverter {
  public PostContract converter(PostEntity entity) {
    PostContract contract =
        new PostContract(
            entity.id,
            entity.userId,
            entity.content,
            entity.creationDate,
            entity.parentId,
            entity.mediaId,
            entity.postType);
    return contract;
  }
}
