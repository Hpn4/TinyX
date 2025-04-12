package com.tinyx.converter;

import com.tinyx.redis.PostQuery;
import com.tinyx.repository.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostQueryToPostEntityConverter {

  /**
   * Convert a PostQuery to a PostEntity.
   *
   * @param postQuery the PostQuery to convert.
   * @return the PostQuery converted into a PostEntity
   */
  public PostEntity convert(PostQuery postQuery) {
    return new PostEntity(postQuery.post.id, postQuery.post.userId);
  }

  /**
   * Convert multiple PostQuery into a list of PostEntity
   *
   * @param postQueryList list of PostQuery to convert
   * @return the list of PostQuery converted into a list of PostEntity
   */
  public List<PostEntity> convert(List<PostQuery> postQueryList) {
    return postQueryList.stream().map(this::convert).collect(Collectors.toList());
  }
}
