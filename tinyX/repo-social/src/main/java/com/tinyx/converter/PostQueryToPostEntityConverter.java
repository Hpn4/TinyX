package com.tinyx.converter;

import com.tinyx.redis.PostQuery;
import com.tinyx.repository.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostQueryToPostEntityConverter {

  public PostEntity convert(PostQuery postQuery) {
    return new PostEntity(postQuery.post.id, postQuery.post.userId);
  }

  public List<PostEntity> convert(List<PostQuery> postQueryList) {
    return postQueryList.stream().map(this::convert).collect(Collectors.toList());
  }
}
