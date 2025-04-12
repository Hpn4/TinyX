package com.tinyx.converter;

import com.tinyx.post.contracts.PostContract;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UuidToPostContractConverter {

  /**
   * Converts UUIDs into a PostContract with the specified user and post IDs.
   *
   * @param userUUID The ID of the user.
   * @param postUUID The ID of the post.
   * @return A PostContract with the provided user and post IDs.
   */
  public PostContract converter(UUID userUUID, UUID postUUID) {
    PostContract postContract = new PostContract();
    postContract.id = postUUID;
    postContract.userId = userUUID;
    return postContract;
  }
}
