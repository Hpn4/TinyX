package com.tinyx.service;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.converter.PostContractToPostEntityConverter;
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
  @Inject PostContractToPostEntityConverter postContractToPostEntityConverter;

  public void createPost(List<PostContract> posts) {
    List<PostEntity> postEntities =
        posts.stream()
            .map(postContractToPostEntityConverter::converter)
            .collect(Collectors.toList());

    postRepository.createPost(postEntities);
  }

  public void deletePost(List<UUID> ids) {
    postRepository.deletePost(ids);
  }

  public void updatePost(List<PostContract> posts) {
    List<PostEntity> postEntities =
        posts.stream()
            .map(postContractToPostEntityConverter::converter)
            .collect(Collectors.toList());

    postRepository.updatePost(postEntities);
  }
}
