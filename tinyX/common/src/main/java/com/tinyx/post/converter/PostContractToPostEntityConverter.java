package com.tinyx.post.converter;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostContractToPostEntityConverter {
  public PostEntity converter(PostContract contract) {
    return new PostEntity(
        contract.id,
        contract.userId,
        contract.content,
        contract.creationDate,
        contract.parentId,
        contract.mediaId,
        contract.postType,
        new ArrayList<UUID>());
  }

  public List<PostEntity> converter(List<PostContract> postsContracts) {
    return postsContracts.stream().map(this::converter).collect(Collectors.toList());
  }
}
