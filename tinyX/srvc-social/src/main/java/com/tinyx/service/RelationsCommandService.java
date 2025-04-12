package com.tinyx.service;

import com.tinyx.ErrorCodes;
import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.repository.LookupRepository;
import com.tinyx.repository.publisher.LikePublisher;
import com.tinyx.repository.publisher.SocialPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.UUID;

@ApplicationScoped
public class RelationsCommandService {

  @Inject LookupRepository lookupRepository;

  @Inject LikePublisher likePublisher;

  @Inject SocialPublisher socialPublisher;

  /**
   * Returns whether there is a block relation between userId and authorId. This function also check
   * if both users are valid UUID and if they exist. If not it will throw a WRONG_UUID or
   * USERS_NOT_FOUND error
   *
   * @param userId The first user
   * @param authorId The second user
   * @return true if there is a block relationship between them, false otherwise
   */
  public boolean blockRelations(final UUID userId, final UUID authorId) {
    if (userId == null) ErrorCodes.WRONG_UUID.throwError("userId");

    if (authorId == null) ErrorCodes.WRONG_UUID.throwError("authorId");

    LookupRepository.BulkReadStatus bulkReadStatus =
        lookupRepository.checkUsersExistAndNoBlock(userId, authorId);

    if (bulkReadStatus == LookupRepository.BulkReadStatus.USERS_NOT_FOUND)
      ErrorCodes.USERS_NOT_FOUND.throwError();

    return bulkReadStatus == LookupRepository.BulkReadStatus.BLOCKED;
  }

  /**
   * Retrieves the auhorID of the given post.
   *
   * @param postId The UUID of the post to get the userID.
   * @return The UUID of the user.
   */
  public UUID getUserIdFromPost(final UUID postId) {
    if (postId == null) ErrorCodes.WRONG_UUID.throwError("postId");

    final UUID userId = lookupRepository.getUserIdFromPost(postId);
    if (userId == null) ErrorCodes.POST_NOT_FOUND.throwError(postId);

    return userId;
  }

  /**
   * Retrieves the auhorID of the given post.
   *
   * @param postId The UUID of the post to get the userID.
   * @return The UUID of the user.
   */
  public Integer likePost(UUID userId, UUID postId) {
    final UUID postOwnerId = getUserIdFromPost(postId);

    if (blockRelations(userId, postOwnerId)) ErrorCodes.BLOCKED_USER.throwError(postOwnerId);

    if (userId.equals(postOwnerId)) ErrorCodes.CANNOT_SELF_POST.throwError(userId, "like");

    if (lookupRepository.checkLikeExist(userId, postId))
      ErrorCodes.ALREADY_LIKED_POST.throwError(userId, postId);

    likePublisher.publish(LikePostQuery.Operation.LIKE, userId, postId, ZonedDateTime.now());

    return lookupRepository.getNumberOfLike(postId);
  }

  /**
   * Removes a like from a post.
   *
   * @param userId The UUID of the user unliking the post.
   * @param postId The UUID of the post being unliked.
   * @return The updated number of likes for the post.
   */
  public Integer unlikePost(UUID userId, UUID postId) {
    final UUID postOwnerId = getUserIdFromPost(postId);

    if (blockRelations(userId, postOwnerId)) ErrorCodes.BLOCKED_USER.throwError(postOwnerId);

    if (userId.equals(postOwnerId)) ErrorCodes.CANNOT_SELF_POST.throwError(userId, "unlike");

    if (!lookupRepository.checkLikeExist(userId, postId))
      ErrorCodes.NO_LIKE.throwError(userId, postId);

    likePublisher.publish(LikePostQuery.Operation.UNLIKE, userId, postId, ZonedDateTime.now());

    return lookupRepository.getNumberOfLike(postId);
  }

  /**
   * Follows a user and publishes the follow relation.
   *
   * @param userId The UUID of the user following the target user.
   * @param targetUserId The UUID of the user to be followed.
   */
  public void followUser(UUID userId, UUID targetUserId) {
    if (blockRelations(userId, targetUserId)) ErrorCodes.BLOCKED_USER.throwError(targetUserId);

    if (userId.equals(targetUserId)) ErrorCodes.CANNOT_SELF.throwError(userId, "follow");

    if (lookupRepository.checkRelationsExist(userId, targetUserId, "FOLLOW"))
      ErrorCodes.ALREADY_FOLLOWED_USER.throwError(targetUserId);

    socialPublisher.publish(
        UserRelationsQuery.Operation.FOLLOW, userId, targetUserId, ZonedDateTime.now());
  }

  /**
   * Unfollows a user and publishes the unfollow relation.
   *
   * @param userId The UUID of the user unfollowing the target user.
   * @param targetUserId The UUID of the user to be unfollowed.
   */
  public void unfollowUser(UUID userId, UUID targetUserId) {
    if (blockRelations(userId, targetUserId)) ErrorCodes.BLOCKED_USER.throwError(targetUserId);

    if (userId.equals(targetUserId)) ErrorCodes.CANNOT_SELF.throwError(userId, "unfollow");

    if (!lookupRepository.checkRelationsExist(userId, targetUserId, "FOLLOW"))
      ErrorCodes.NO_FOLLOWED_USER.throwError(targetUserId);

    socialPublisher.publish(
        UserRelationsQuery.Operation.UNFOLLOW, userId, targetUserId, ZonedDateTime.now());
  }

  /**
   * Blocks a user and publishes the block relation.
   *
   * @param userId The UUID of the user blocking the target user.
   * @param targetUserId The UUID of the user to be blocked.
   */
  public void blockUser(UUID userId, UUID targetUserId) {
    if (blockRelations(userId, targetUserId))
      ErrorCodes.ALREADY_BLOCKED_USER.throwError(targetUserId);

    if (userId.equals(targetUserId)) ErrorCodes.CANNOT_SELF.throwError(userId, "block");

    socialPublisher.publish(
        UserRelationsQuery.Operation.BLOCK, userId, targetUserId, ZonedDateTime.now());
  }

  /**
   * Unblock a user and publishes the unblock relation.
   *
   * @param userId The UUID of the user unblocking the target user.
   * @param targetUserId The UUID of the user to be unblocked.
   */
  public void unblockUser(UUID userId, UUID targetUserId) {
    if (!blockRelations(userId, targetUserId)) ErrorCodes.NO_BLOCKED_USER.throwError(targetUserId);

    if (userId.equals(targetUserId)) ErrorCodes.CANNOT_SELF.throwError(userId, "unblock");

    socialPublisher.publish(
        UserRelationsQuery.Operation.UNBLOCK, userId, targetUserId, ZonedDateTime.now());
  }
}
