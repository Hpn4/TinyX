package com.tinyx.converter;

import com.tinyx.post.contracts.PostContract;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UuidToPostContractConverter {
  public PostContract converter(UUID userUUID, UUID postUUID) {
    PostContract postContract = new PostContract();
    postContract.id = postUUID;
    postContract.userId = userUUID;
    return postContract;
  }
}
