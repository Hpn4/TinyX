package com.tinyx.service;

import com.tinyx.ErrorCodes;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.PostRestClient;
import com.tinyx.repository.RelationsRepository;
import com.tinyx.repository.UserRestClient;
import com.tinyx.user.contracts.UserContract;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

@ApplicationScoped
public class RelationsQueryService {

  @Inject RelationsRepository relationsRepository;

  @Inject RelationsCommandService commandService;

  @RestClient UserRestClient userRestClient;

  @RestClient PostRestClient postRestClient;

  private List<UserContract> getUsers(List<UUID> userIds) {
    try {
      return userRestClient.getUsersByIds(userIds);
    } catch (ClientWebApplicationException e) {
      ErrorCodes.UNREACHABLE.throwError("srvc-user");
    }

    return null;
  }

  private List<PostContract> getPosts(final UUID userId, final List<UUID> postsIds) {
    try {
      return postRestClient.queryPostsList(userId, postsIds);
    } catch (ClientWebApplicationException e) {
      ErrorCodes.UNREACHABLE.throwError("srvc-ppost");
    }

    return null;
  }

  public List<UserContract> getLikers(UUID postId, UUID userId) {
    final UUID postOwnerId = commandService.getUserIdFromPost(postId);

    if (commandService.blockRelations(userId, postOwnerId))
      ErrorCodes.BLOCKED_USER.throwError(postOwnerId);

    return getUsers(relationsRepository.getLikers(postId, userId));
  }

  public List<PostContract> getLikedPost(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getPosts(userId, relationsRepository.getLikedPosts(targetId, userId));
  }

  public List<UserContract> getUserFollows(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getUserFollow(targetId, userId));
  }

  public List<UserContract> getFollowers(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getFollowers(targetId, userId));
  }

  public List<UserContract> getBlockedUsers(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getBlockedUsers(targetId, userId));
  }

  public List<UserContract> getTargetBlock(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getTargetBlock(targetId, userId));
  }
}
