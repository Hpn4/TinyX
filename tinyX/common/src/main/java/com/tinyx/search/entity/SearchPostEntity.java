package com.tinyx.search.entity;

import java.util.List;
import java.util.UUID;

public class SearchPostEntity {
  public UUID postId;

  public String text;

  public List<String> hashtags;

  public SearchPostEntity() {}

  /**
   * Constructs a new SearchPostEntity with the specified post ID, content, and hashtags.
   *
   * @param postId The unique identifier of the post.
   * @param content The text content of the post.
   * @param hashtags A list of hashtags associated with the post.
   */
  public SearchPostEntity(final UUID postId, final String content, final List<String> hashtags) {
    this.postId = postId;
    this.text = content;
    this.hashtags = hashtags;
  }
}
