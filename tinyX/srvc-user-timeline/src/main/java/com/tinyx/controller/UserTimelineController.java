package com.tinyx.controller;

import com.tinyx.ErrorCodes;
import com.tinyx.service.UserTimelineService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/timeline")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserTimelineController {

  @Inject UserTimelineService userTimelineService;

  /**
   * Returns the timeline of the given user, including both authored and liked posts, sorted by
   * date.
   *
   * @param userId The user UUID to fetch the timeline from.
   * @return A list of {@link com.tinyx.post.contracts.PostContract} objects representing the
   *     timeline of the specified user, including both authored and liked posts, sorted by date.
   */
  @Path("/user")
  @GET
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad UUID"),
    @APIResponse(responseCode = "404", description = "User does not exist")
  })
  public Response getUserTimeline(@HeaderParam("X-User") UUID userId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(userTimelineService.getUserTimeline(userId)).build();
  }

  /**
   * Returns a merged timeline of the given users, including both authored and liked posts, sorted
   * by date. The timeline is a combination of each user's timeline, sorted by the creation date of
   * the posts for authored posts and by the date of the like for liked posts. The list may contain
   * duplicates, and posts from blocked users (blocked by the authenticated user) are filtered out.
   *
   * @param userId The authenticated user UUID, used to filter out posts from users that the
   *     authenticated user has blocked.
   * @param usersId A list of user UUIDs whose timelines (authored and liked posts) are to be
   *     merged.
   * @return A list of {@link com.tinyx.post.contracts.PostContract} objects representing the merged
   *     and sorted timeline for the specified users, including authored and liked posts,
   *     potentially with duplicates, and excluding posts from blocked users.
   */
  @Path("/users")
  @GET
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad UUID"),
    @APIResponse(responseCode = "404", description = "One or multiple users does not exist")
  })
  public Response getUsersTimeline(@HeaderParam("X-User") UUID userId, List<UUID> usersId) {
    if (usersId == null || usersId.isEmpty()) ErrorCodes.WRONG_UUID.throwError("usersId");

    if (userId == null) ErrorCodes.WRONG_UUID.throwError("userId");

    return Response.ok(userTimelineService.getUsersTimeline(userId, usersId)).build();
  }
}
