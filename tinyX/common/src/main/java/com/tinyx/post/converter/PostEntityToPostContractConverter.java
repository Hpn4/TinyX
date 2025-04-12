package com.tinyx.post.converter;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostEntityToPostContractConverter {
  public PostContract convert(PostEntity entity) {
    return new PostContract(
        entity.id,
        entity.userId,
        entity.content,
        entity.creationDate,
        entity.parentId,
        entity.mediaId,
        entity.postType);
  }

  public List<PostContract> convert(List<PostEntity> postsContracts) {
    return postsContracts.stream().map(this::convert).collect(Collectors.toList());
  }
}
