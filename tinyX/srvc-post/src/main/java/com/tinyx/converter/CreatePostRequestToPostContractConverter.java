package com.tinyx.converter;

import com.tinyx.controller.request.CreatePostRequest;
import com.tinyx.post.contracts.PostContract;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Handle conversion between http PostContract/UUID of a post into a common PostContract between
 * different modules Purely post related.
 */
@ApplicationScoped
public class CreatePostRequestToPostContractConverter {

  /**
   * Converts a CreatePostRequest into a PostContract.
   *
   * @param contract The CreatePostRequest to convert.
   * @param userId The ID of the user creating the post.
   * @return A PostContract representing the new post.
   */
  public PostContract converter(CreatePostRequest contract, UUID userId) {
    PostContract postContract =
        new PostContract(
            UUID.randomUUID(),
            userId,
            contract.content,
            ZonedDateTime.now(),
            contract.parentId,
            contract.mediaId,
            contract.postType);
    return postContract;
  }
}
