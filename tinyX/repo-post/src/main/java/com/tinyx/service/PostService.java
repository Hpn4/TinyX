package com.tinyx.service;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.converter.PostContractToPostEntityConverter;
import com.tinyx.post.entity.PostEntity;
import com.tinyx.repository.PostRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;

@ApplicationScoped
public class PostService {
  @Inject PostRepository postRepository;
  @Inject PostContractToPostEntityConverter postContractToPostEntityConverter;

  /**
   * Call the appropriate repository function to handle the creation
   * @param posts List of PostContract to be created
   */
  public void createPost(List<PostContract> posts) {
    List<PostEntity> postEntities = postContractToPostEntityConverter.converter(posts);

    postRepository.createPost(postEntities);
  }

  /**
   * Call the delete function from the repository
   * @param ids The ids of PostEntity to be deleted
   */
  public void deletePost(List<UUID> ids) {
    postRepository.deletePost(ids);
  }
}
