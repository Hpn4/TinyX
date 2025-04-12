package com.tinyx.controller;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.service.SvcHomeTimelineService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HomeTimelineController {
  @Inject SvcHomeTimelineService service;

  @Inject Logger logger;

  /**
   * The endpoint get the timeline (a list of post) of a specific user.
   *
   * @param userId id of the user. Retrieved by the header parameter X-User
   * @return the list of post shaping the user timeline.
   */
  @GET
  @Path("/timeline/home")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Null userId"),
    @APIResponse(responseCode = "404", description = "User not found"),
    @APIResponse(responseCode = "500", description = "Internal rest client unreachable")
  })
  public Response UserTimeline(@HeaderParam("X-User") UUID userId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();

    List<PostContract> timeline;

    try {
      timeline = service.GetUserTimeline(userId);
    } catch (NotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    } catch (ClientWebApplicationException e) {
      logger.error(e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    return Response.ok(timeline).build();
  }
}
