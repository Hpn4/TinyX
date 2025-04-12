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

  /**
   * Converts a PostContract object into a PostEntity object.
   *
   * @param contract The PostContract object to be converted.
   * @return The resulting PostEntity object, populated with values from the given contract.
   */
  public PostEntity convert(PostContract contract) {
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

  /**
   * Converts a list of PostContract objects into a list of PostEntity objects.
   *
   * @param postsContracts The list of PostContract objects to be converted.
   * @return A list of PostEntity objects, each populated with values from the respective
   *     PostContract.
   */
  public List<PostEntity> convert(List<PostContract> postsContracts) {
    return postsContracts.stream().map(this::convert).collect(Collectors.toList());
  }
}
