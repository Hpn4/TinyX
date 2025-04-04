package com.tinyx.controller;

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
   * Return the user timeline of the given user (authored posts and liked posts) sorted by date
   *
   * @param userId the user to get the timeline
   */
  @Path("/user")
  @GET
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user"),
    @APIResponse(responseCode = "404", description = "User does not exist")
  })
  public Response getUserTimeline(@HeaderParam("X-User") UUID userId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(userTimelineService.getUserTimeline(userId)).build();
  }

  /**
   * Return a timeline of the given users (authored posts and liked posts) sorted by date
   *
   * @param usersId users to get the timeline.
   * @return A timeline containing all the timeline of each user sorted by date
   */
  @Path("/users")
  @GET
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user"),
    @APIResponse(responseCode = "404", description = "User does not exist")
  })
  public Response getUsersTimeline(List<UUID> usersId) {
    if (usersId == null || usersId.isEmpty())
      return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(userTimelineService.getUsersTimeline(usersId)).build();
  }
}
