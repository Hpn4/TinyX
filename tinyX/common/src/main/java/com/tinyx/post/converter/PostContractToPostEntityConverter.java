package com.tinyx.post.converter;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.UUID;

@ApplicationScoped
public class PostContractToPostEntityConverter {
  public PostEntity converter(PostContract contract) {
    PostEntity entity =
        new PostEntity(
            contract.id,
            contract.userId,
            contract.content,
            contract.creationDate,
            contract.parentId,
            contract.mediaId,
            contract.postType,
            new ArrayList<UUID>());
    return entity;
  }
}
