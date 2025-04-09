package com.tinyx.converter;

import com.tinyx.redis.PostQuery;
import com.tinyx.repository.entity.PostEntity;
import java.util.List;
import java.util.stream.Collectors;

public class PostQueryToPostEntityConverter {

  public PostEntity convert(PostQuery postQuery) {
    return new PostEntity(postQuery.post.id, postQuery.post.userId);
  }

  public List<PostEntity> convert(List<PostQuery> postQueryList) {
    return postQueryList.stream().map(this::convert).collect(Collectors.toList());
  }
}
