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

  /**
   *
   * @param posts List of posts to create
   */
  public void createPost(List<PostContract> posts) {
    List<PostEntity> postEntities =
        posts.stream().map(postConverter::convertPost).collect(Collectors.toList());

    postRepository.createPost(postEntities);
  }

  /**
   *
   * @param ids List of ids of posts to delete
   */
  public void deletePost(List<UUID> ids) {
    postRepository.deletePost(ids);
  }

  /**
   *
   * @param posts List of posts to update
   */
  public void updatePost(List<PostContract> posts) {
    List<PostEntity> postEntities =
        posts.stream().map(postConverter::convertPost).collect(Collectors.toList());

    postRepository.updatePost(postEntities);
  }
}
