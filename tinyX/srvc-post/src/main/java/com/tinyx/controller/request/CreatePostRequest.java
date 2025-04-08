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

  public CreatePostRequest(String content, UUID parentId, UUID mediaId, PostType postType) {
    this.content = content;
    this.parentId = parentId;
    this.mediaId = mediaId;
    this.postType = postType;
  }
}
