package com.tinyx.search.entity;

import java.util.List;
import java.util.UUID;

public class SearchPostEntity {
  public UUID postId;

  public String text;

  public List<String> hashtags;

  public SearchPostEntity() {}

  public SearchPostEntity(final UUID postId, final String content, final List<String> hashtags) {
    this.postId = postId;
    this.text = content;
    this.hashtags = hashtags;
  }
}
