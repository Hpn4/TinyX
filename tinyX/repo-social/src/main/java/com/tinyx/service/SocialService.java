package com.tinyx.service;

import com.tinyx.converter.LikePostQueryToSocialRelationEntityConverter;
import com.tinyx.converter.PostQueryToPostEntityConverter;
import com.tinyx.converter.UserRelationsQueryToSocialRelationEntityConverter;
import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.repository.RelationsRepository;
import com.tinyx.repository.SocialRepository;
import com.tinyx.repository.UnfollowPublisher;
import com.tinyx.repository.UnlikePublisher;
import com.tinyx.repository.entity.PostEntity;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SocialService {

  @Inject SocialRepository socialRepository;

  @Inject RelationsRepository relationsRepository;

  @Inject PostQueryToPostEntityConverter postQueryToPostEntityConverter;

  @Inject LikePostQueryToSocialRelationEntityConverter likePostQueryToSocialRelationEntityConverter;

  @Inject
  UserRelationsQueryToSocialRelationEntityConverter
      userRelationsQueryToSocialRelationEntityConverter;

  @Inject UnlikePublisher unlikePublisher;

  @Inject UnfollowPublisher unfollowPublisher;

  public void createPosts(List<PostQuery> queries) {
    socialRepository.createPosts(postQueryToPostEntityConverter.convert(queries));
  }

  public void deletePosts(List<PostQuery> queries) {
    List<PostEntity> postsEntities = postQueryToPostEntityConverter.convert(queries);

    // Delete the post and all his likes relations
    socialRepository.deletePosts(postsEntities);
  }

  public void createUsers(List<UUID> userIds) {
    socialRepository.createUsers(userIds);
  }

  public void likeRelations(List<LikePostQuery> queries) {
    relationsRepository.createLikeRelations(
        likePostQueryToSocialRelationEntityConverter.convert(queries));
  }

  public void unlikeRelations(List<LikePostQuery> queries) {
    List<SocialRelationEntity> unlikeRelations =
        likePostQueryToSocialRelationEntityConverter.convert(queries);

    relationsRepository.deleteRelations(unlikeRelations, "LIKE", "User", "Post");
  }

  public void followRelations(List<UserRelationsQuery> queries) {
    relationsRepository.createFollowRelations(
        userRelationsQueryToSocialRelationEntityConverter.convert(queries));
  }

  public void unfollowRelations(List<UserRelationsQuery> queries) {
    List<SocialRelationEntity> unfollowRelations =
        userRelationsQueryToSocialRelationEntityConverter.convert(queries);

    relationsRepository.deleteRelations(unfollowRelations, "FOLLOW", "User", "User");
  }

  public void blockRelations(List<UserRelationsQuery> queries) {
    List<SocialRelationEntity> blockRelations =
        userRelationsQueryToSocialRelationEntityConverter.convert(queries);

    for (SocialRelationEntity sre : blockRelations) {
      // Blocked users implies UNFOLLOW in both directions
      unfollowPublisher.publish(sre.srcId, sre.targetId);
      unfollowPublisher.publish(sre.targetId, sre.srcId);

      // UNLIKE all posts srcId user has with the new blocked user
      for (UUID postId : socialRepository.getPostIdsFromUser(sre.srcId, sre.targetId)) {
        unlikePublisher.publish(sre.srcId, postId);
      }
    }

    relationsRepository.createBlockRelations(blockRelations);
  }

  public void unblockRelations(List<UserRelationsQuery> queries) {
    List<SocialRelationEntity> unblockRelations =
        userRelationsQueryToSocialRelationEntityConverter.convert(queries);

    relationsRepository.deleteRelations(unblockRelations, "BLOCK", "User", "User");
  }
}
