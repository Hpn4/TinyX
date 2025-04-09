package com.tinyx.requests;

import com.tinyx.post.enumeration.PostType;
import java.util.UUID;

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
