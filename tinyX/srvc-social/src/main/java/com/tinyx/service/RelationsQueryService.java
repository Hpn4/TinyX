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

  public List<LightUserContract> getLikers(UUID postId, UUID userId) {
    final UUID postOwnerId = commandService.getUserIdFromPost(postId);

    if (commandService.blockRelations(userId, postOwnerId))
      ErrorCodes.BLOCKED_USER.throwError(postOwnerId);

    List<UUID> lists = relationsRepository.getLikers(postId, userId);

    return getUsers(lists);
  }

  public List<PostContract> getLikedPost(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getPosts(userId, relationsRepository.getLikedPosts(targetId, userId));
  }

  public List<LightUserContract> getUserFollows(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getUserFollow(targetId, userId));
  }

  public List<LightUserContract> getFollowers(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getFollowers(targetId, userId));
  }

  public List<LightUserContract> getBlockedUsers(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getBlockedUsers(targetId, userId));
  }

  public List<LightUserContract> getTargetBlock(UUID targetId, UUID userId) {
    if (commandService.blockRelations(userId, userId)) ErrorCodes.BLOCKED_USER.throwError(userId);

    return getUsers(relationsRepository.getTargetBlock(targetId, userId));
  }
}
