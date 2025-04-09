package com.tinyx.converter;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LikePostQueryToSocialRelationEntityConverter {

  public SocialRelationEntity convert(LikePostQuery likePostQuery) {
    return new SocialRelationEntity(
        likePostQuery.srcUserId, likePostQuery.targetPostId, likePostQuery.creationDate);
  }

  public List<SocialRelationEntity> convert(List<LikePostQuery> likePostQueryList) {
    return likePostQueryList.stream().map(this::convert).collect(Collectors.toList());
  }
}
