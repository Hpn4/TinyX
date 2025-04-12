package com.tinyx.controller.request;

import com.tinyx.post.enumeration.PostType;
import java.util.UUID;

/** Post contract for PostController REST API */
public class CreatePostRequest {
  public String content;
  public UUID parentId;
  public UUID mediaId;
  public PostType postType;

  public CreatePostRequest() {}

  /**
   * Constructs a CreatePostRequest with the given parameters.
   *
   * @param content The content of the post.
   * @param parentId The ID of the parent post, if any.
   * @param mediaId The ID of the media associated with the post, if any.
   * @param postType The type of the post.
   */
  public CreatePostRequest(String content, UUID parentId, UUID mediaId, PostType postType) {
    this.content = content;
    this.parentId = parentId;
    this.mediaId = mediaId;
    this.postType = postType;
  }
}
