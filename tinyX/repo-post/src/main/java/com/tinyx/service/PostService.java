package com.tinyx.service;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.converter.PostContractToPostEntityConverter;
import com.tinyx.post.entity.PostEntity;
import com.tinyx.repository.PostRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PostService {
  @Inject PostRepository postRepository;
  @Inject PostContractToPostEntityConverter postContractToPostEntityConverter;

  @Inject Logger log;

  public void createPost(List<PostContract> posts) {
    List<PostEntity> postEntities = postContractToPostEntityConverter.convert(posts);

    postRepository.createPost(postEntities);
  }

  public void deletePost(List<UUID> ids) {
    postRepository.deletePost(ids);
  }
}
