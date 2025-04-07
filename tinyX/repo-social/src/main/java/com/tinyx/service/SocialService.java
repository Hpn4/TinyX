package com.tinyx.service;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import com.tinyx.repository.SocialRepository;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SocialService {

  @Inject SocialRepository repoSocialRepository;

  @Inject
  RedisPublisherFactory redisPublisherFactory;
  public void createPosts(List<UUID> lpc) {
    if (lpc.isEmpty()) return;
    repoSocialRepository.createPosts(lpc);
  }

  public void deletePosts(List<UUID> lpc) {
    if (lpc.isEmpty()) return;
    for(var i = 0; i<lpc.size();i++)
    {
      List<UUID> userIds = repoSocialRepository.getUsersId(lpc.get(i));
      for(var j = 0; j< userIds.size();j++)
      {
        LikePostQuery lpq = new LikePostQuery(LikePostQuery.Operation.UNLIKE,userIds.get(j),lpc.get(i), ZonedDateTime.now());
        redisPublisherFactory.<LikePostQuery>createPublisher().publishStream(RedisChannel.LIKE,lpq, LikePostQuery.class);
      }
    }
    repoSocialRepository.deletePosts(lpc);
  }

  public void createUsers(List<UUID> luc) {
    if (luc.isEmpty()) return;
    repoSocialRepository.createUsers(luc);
  }

  public void deleteUsers(List<UUID> luc) {
    if (luc.isEmpty()) return;
    repoSocialRepository.deleteUsers(luc);
  }

  public void createRelations(
      List<SocialRelationEntity> lre, String relation, String t1, String t2) {
    if (lre.isEmpty()) return;
    if(relation == "BLOCK") {
      for (var i = 0; i < lre.size(); i++) {
        UserRelationsQuery unfolowAfromB = new UserRelationsQuery(UserRelationsQuery.Operation.UNFOLLOW, lre.get(i).srcId, lre.get(i).targetId, ZonedDateTime.now());
        redisPublisherFactory.<UserRelationsQuery>createPublisher().publishStream(RedisChannel.SOCIAL, unfolowAfromB, UserRelationsQuery.class);
        UserRelationsQuery unfollowBfromA = new UserRelationsQuery(UserRelationsQuery.Operation.UNFOLLOW, lre.get(i).targetId, lre.get(i).srcId, ZonedDateTime.now());
        redisPublisherFactory.<UserRelationsQuery>createPublisher().publishStream(RedisChannel.SOCIAL, unfollowBfromA, UserRelationsQuery.class);
      }
    }
    repoSocialRepository.createRelations(lre, relation, t1, t2);
  }

  public void deleteRelations(
      List<SocialRelationEntity> lre, String relation, String t1, String t2) {
    if (lre.isEmpty()) return;
    repoSocialRepository.deleteRelations(lre, relation, t1, t2);
  }
}
