package com.tinyx.converter;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LikePostQueryToSocialRelationEntityConverter {

  /**
   * Convert a LikePostQuery to a SocialRelationEntity.
   *
   * @param likePostQuery the LikePostQuery to convert.
   * @return the LikePostQuery converted into a SocialRelationEntity
   */
  public SocialRelationEntity convert(LikePostQuery likePostQuery) {
    return new SocialRelationEntity(
        likePostQuery.srcUserId, likePostQuery.targetPostId, likePostQuery.creationDate);
  }

  /**
   * Convert multiple LikePostQuery into a list of SocialRelationEntity
   *
   * @param likePostQueryList list of LikePostQuery to convert
   * @return the list of LikePostQuery converted into a list of SocialRelationEntity
   */
  public List<SocialRelationEntity> convert(List<LikePostQuery> likePostQueryList) {
    return likePostQueryList.stream().map(this::convert).collect(Collectors.toList());
  }
}
