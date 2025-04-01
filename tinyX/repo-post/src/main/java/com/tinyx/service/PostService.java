package com.tinyx.service;

import com.tinyx.post.PostConverter;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.entity.PostEntity;
import com.tinyx.repository.PostRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostService {
  @Inject PostRepository postRepository;
  @Inject PostConverter postConverter;

  public boolean isPostValid(PostContract postContract) {
    return !(postContract.id == null
        || postContract.userId == null
        || postContract.content == null
        || postContract.creationDate == null);
  }

  public void createPost(List<PostContract> posts) {
    posts.removeIf(post -> !isPostValid(post));
    List<PostEntity> postEntities =
        posts.stream().map(postConverter::convertPost).collect(Collectors.toList());

    postRepository.createPost(postEntities);
  }

  public long deletePost(List<UUID> ids) {
    return postRepository.deletePost(ids);
  }

  public void updatePost(List<PostContract> posts) {
    posts.removeIf(post -> !isPostValid(post));
    List<PostEntity> postEntities =
        posts.stream().map(postConverter::convertPost).collect(Collectors.toList());

    postRepository.updatePost(postEntities);
  }
}
