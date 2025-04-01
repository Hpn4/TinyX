package com.tinyx.service;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.PostRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class PostService {
  @Inject PostRepository postRepository;

  public void createPost(PostContract post) {}

  public UUID deletePost(UUID id) {
    return null;
  }

  public void updatePost(PostContract post) {}
}
