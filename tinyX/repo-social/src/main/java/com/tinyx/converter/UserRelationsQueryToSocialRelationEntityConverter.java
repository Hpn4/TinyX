package com.tinyx.converter;

import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserRelationsQueryToSocialRelationEntityConverter {

  public SocialRelationEntity convert(UserRelationsQuery userRelationsQuery) {
    return new SocialRelationEntity(
        userRelationsQuery.srcUserId,
        userRelationsQuery.targetUserId,
        userRelationsQuery.creationDate);
  }

  public List<SocialRelationEntity> convert(List<UserRelationsQuery> userRelationsQueryList) {
    return userRelationsQueryList.stream().map(this::convert).collect(Collectors.toList());
  }
}
