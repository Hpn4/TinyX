package com.tinyx.service;

import com.tinyx.ErrorCodes;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.PostRestClient;
import com.tinyx.repository.RelationsRepository;
import com.tinyx.repository.UserRestClient;
import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.converter.UserContractToLightUserContractConverter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

@ApplicationScoped
public class RelationsQueryService {

  @Inject UserContractToLightUserContractConverter lightUserConverter;

  @Inject RelationsRepository relationsRepository;

  @Inject RelationsCommandService commandService;

  @RestClient UserRestClient userRestClient;

  @RestClient PostRestClient postRestClient;

  @Inject Logger log;

  /**
   * Retrieves a list of user Contract based on their UUIDs.
   *
   * @param userIds The list of user UUIDs to convert.
   * @return LightUserContract from userID list.
   */
  private List<LightUserContract> getUsers(List<UUID> userIds) {
    if (userIds == null || userIds.isEmpty()) return new ArrayList<>();

    try {
      return lightUserConverter.convert(userRestClient.getUsersByIds(userIds));
    } catch (ClientWebApplicationException e) {
      if (e.getResponse().getStatus() == 404) ErrorCodes.USERS_NOT_FOUND.throwError();

      ErrorCodes.UNREACHABLE.throwError("srvc-user");
    }

    return null;
  }

  /**
   * Retrieves a list of posts based on the provided user and post UUIDs.
   *
   * @param userId The ID of the user requesting the posts.
   * @param postsIds The list of post UUIDs to retrieve.
   * @return List of PostContract (or an empty list if no posts are found).
   */
  private List<PostContract> getPosts(final UUID userId, final List<UUID> postsIds) {
    if (postsIds == null || postsIds.isEmpty()) return new ArrayList<>();

    try {
      return postRestClient.queryPostsList(userId, postsIds);
    } catch (ClientWebApplicationException e) {
      if (e.getResponse().getStatus() == 404) ErrorCodes.POSTS_NOT_FOUND.throwError();

      ErrorCodes.UNREACHABLE.throwError("srvc-ppost");
    }

    return null;
  }

  /**
   * Retrieves a list of users who liked the given postID, filtering out blocked users.
   *
   * @param postId The ID of the post to get likers for.
   * @param userId The ID of the user to filter blocked users.
   * @return List of LightUserContract representing users who liked the post.
   */
  public List<LightUserContract> getLikers(UUID postId, UUID userId) {
    final UUID postOwnerId = commandService.getUserIdFromPost(postId);

    if (commandService.blockRelations(userId, postOwnerId))
      ErrorCodes.BLOCKED_USER.throwError(postOwnerId);

    List<UUID> lists = relationsRepository.getLikers(postId, userId);

    return getUsers(lists);
  }

  /**
   * Retrieves a list of posts liked by a given user, filtering out blocked users.
   *
   * @param targetId The ID of the user whose liked posts are retrieved.
   * @param userId The ID of the user to filter blocked users.
   * @return A list of PostContract objects representing the liked posts.
   */
  public List<PostContract> getLikedPost(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getPosts(userId, relationsRepository.getLikedPosts(targetId, userId));
  }

  /**
   * Retrieves a list of users followed by a given user, excluding blocked users.
   *
   * @param targetId The ID of the user whose followers are retrieved.
   * @param userId The ID of the user to filter blocked users.
   * @return List of LightUserContract representing the followed users.
   */
  public List<LightUserContract> getUserFollows(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getUserFollow(targetId, userId));
  }

  /**
   * Retrieves a list of users following a given user, excluding blocked users.
   *
   * @param targetId The ID of the user whose followers are retrieved.
   * @param userId The ID of the user to filter blocked users.
   * @return List of LightUserContract representing the followers.
   */
  public List<LightUserContract> getFollowers(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getFollowers(targetId, userId));
  }

  /**
   * Retrieves a list of users blocked by a given user, excluding blocked users.
   *
   * @param targetId The ID of the user whose blocked users are retrieved.
   * @param userId The ID of the user to filter blocked users.
   * @return List of LightUserContract representing the blocked users.
   */
  public List<LightUserContract> getBlockedUsers(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getBlockedUsers(targetId, userId));
  }

  /**
   * Retrieves a list of users who have blocked the target user.
   *
   * @param targetId The ID of the target user whose blockers are retrieved.
   * @param userId The ID of the user from exclude the blocked users.
   * @return List of LightUserContract representing the users who blocked the target.
   */
  public List<LightUserContract> getTargetBlock(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getTargetBlock(targetId, userId));
  }
}
