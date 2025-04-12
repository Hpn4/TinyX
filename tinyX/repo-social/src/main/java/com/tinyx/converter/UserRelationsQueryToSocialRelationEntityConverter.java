package com.tinyx.converter;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserRelationsQueryToSocialRelationEntityConverter {

  /**
   * Convert a UserRelationsQuery into a SocialRelationEntity
   *
   * @param userRelationsQuery UserRelationQuery to convert
   * @return the UserRelationsQuery converted into a SocialRelationEntity
   */
  public SocialRelationEntity convert(UserRelationsQuery userRelationsQuery) {
    return new SocialRelationEntity(
        userRelationsQuery.srcUserId,
        userRelationsQuery.targetUserId,
        userRelationsQuery.creationDate);
  }

  /**
   * Convert multiple UserRelationsQuery into a list of SocialRelationEntity
   *
   * @param userRelationsQueryList list of UserRelationsQuery to convert
   * @return the list of UserRelationsQuery converted into a list of SocialRelationEntity
   */
  public List<SocialRelationEntity> convert(List<UserRelationsQuery> userRelationsQueryList) {
    return userRelationsQueryList.stream().map(this::convert).collect(Collectors.toList());
  }
}
