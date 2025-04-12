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

  /**
   * Create multiple posts
   *
   * @param queries list of request to create post
   */
  public void createPosts(List<PostQuery> queries) {
    socialRepository.createPosts(postQueryToPostEntityConverter.convert(queries));
  }

  /**
   * delete multiple posts
   *
   * @param queries list of request to delete post
   */
  public void deletePosts(List<PostQuery> queries) {
    List<PostEntity> postsEntities = postQueryToPostEntityConverter.convert(queries);

    // Delete the post and all his likes relations
    socialRepository.deletePosts(postsEntities);
  }

  /**
   * Create multiple users
   *
   * @param userIds list of users to create
   */
  public void createUsers(List<UUID> userIds) {
    socialRepository.createUsers(userIds);
  }

  /**
   * create like relations
   *
   * @param queries list of request to like post
   */
  public void likeRelations(List<LikePostQuery> queries) {
    relationsRepository.createLikeRelations(
        likePostQueryToSocialRelationEntityConverter.convert(queries));
  }

  /**
   * delete like relations
   *
   * @param queries list of request to unlike post
   */
  public void unlikeRelations(List<LikePostQuery> queries) {
    List<SocialRelationEntity> unlikeRelations =
        likePostQueryToSocialRelationEntityConverter.convert(queries);

    relationsRepository.deleteRelations(unlikeRelations, "LIKE", "User", "Post");
  }

  /**
   * create follow relations
   *
   * @param queries list of request to follow a user
   */
  public void followRelations(List<UserRelationsQuery> queries) {
    relationsRepository.createFollowRelations(
        userRelationsQueryToSocialRelationEntityConverter.convert(queries));
  }

  /**
   * delete follow relations
   *
   * @param queries list of request to unfollow a user
   */
  public void unfollowRelations(List<UserRelationsQuery> queries) {
    List<SocialRelationEntity> unfollowRelations =
        userRelationsQueryToSocialRelationEntityConverter.convert(queries);

    relationsRepository.deleteRelations(unfollowRelations, "FOLLOW", "User", "User");
  }

  /**
   * create block relations
   *
   * @param queries list of request to block a user
   */
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

  /**
   * delete block relations
   *
   * @param queries list of request to unblock a user
   */
  public void unblockRelations(List<UserRelationsQuery> queries) {
    List<SocialRelationEntity> unblockRelations =
        userRelationsQueryToSocialRelationEntityConverter.convert(queries);

    relationsRepository.deleteRelations(unblockRelations, "BLOCK", "User", "User");
  }
}
