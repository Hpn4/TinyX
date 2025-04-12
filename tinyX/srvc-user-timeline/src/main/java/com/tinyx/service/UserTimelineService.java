package com.tinyx.service;

import com.tinyx.ErrorCodes;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.PostRestClient;
import com.tinyx.repository.UserTimelineRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

@ApplicationScoped
public class UserTimelineService {

  @Inject UserTimelineRepository repository;

  @RestClient PostRestClient postRestClient;

  /**
   * Retrieves a list of posts by their UUIDs and returns them as {@link
   * com.tinyx.post.contracts.PostContract} objects. If the Post service is unreachable, an error is
   * thrown. This functions filter out posts from a blocked user.
   *
   * @param authUserId The UUID of the authenticated user, used to filter posts.
   * @param postsId A list of post UUIDs to retrieve.
   * @return A list of {@link com.tinyx.post.contracts.PostContract} objects representing the
   *     requested posts.
   */
  public List<PostContract> getPostsContract(final UUID authUserId, final List<UUID> postsId) {
    if (postsId == null || postsId.isEmpty()) return new ArrayList<>();

    try {
      return postRestClient.queryPostsList(authUserId, postsId);
    } catch (ClientWebApplicationException e) {
      if (e.getResponse().getStatus() == 404) ErrorCodes.POSTS_NOT_FOUND.throwError();

      ErrorCodes.UNREACHABLE.throwError("srvc-post");
    }

    return null;
  }

  /**
   * Retrieves the timeline for a specific user, including both authored and liked posts. The method
   * ensures the user exists before fetching their timeline.
   *
   * @param userId The ID of the user whose timeline is to be retrieved.
   * @return A list of {@link PostContract} objects representing the user's ordered timeline.
   */
  public List<PostContract> getUserTimeline(final UUID userId) {
    // Check that the user exists
    repository.findByIdOptional(userId).orElseThrow(ErrorCodes.USER_NOT_FOUND.asSupplier(userId));

    return getPostsContract(userId, repository.findOrderedPostsForUser(userId));
  }

  /**
   * Retrieves and merges the timelines of multiple users, excluding the authenticated user's own
   * timeline. The method ensures all users exist and then fetches their posts.
   *
   * @param userId The UUID of the authenticated user.
   * @param usersId A list of user UUIDs whose timelines are to be retrieved and merged.
   * @return A list of {@link PostContract} objects representing the merged and ordered timelines of
   *     the specified users.
   */
  public List<PostContract> getUsersTimeline(final UUID userId, final List<UUID> usersId) {
    // We remove duplicates and add the authenticate user UUID to test for existence of users in one
    // go
    Set<UUID> distinctUsersIds = new HashSet<>(usersId);
    distinctUsersIds.add(userId);

    long count = repository.count("_id in ?1", distinctUsersIds);
    if (count != distinctUsersIds.size()) ErrorCodes.USERS_NOT_FOUND.throwError();

    // We then remove the users since we don't want to include his timeline, it was just in order to
    // run a single mongo query
    distinctUsersIds.remove(userId);

    return getPostsContract(
        userId, repository.findOrderedPostsByUsers(distinctUsersIds.stream().toList()));
  }
}
