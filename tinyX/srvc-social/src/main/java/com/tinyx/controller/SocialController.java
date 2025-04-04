package com.tinyx.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

/**
 * Handles all the endpoints that are purely post related. Maybe a new endpoint could be later added
 * to update user data (c.f. Miro)
 */
@Path("/social")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SocialController {

  /**
   * Like a post.
   *
   * @param userId the ID of the user liking the post
   * @param postId the ID of the post to be liked
   * @return Response indicating the result of the operation
   */
  @POST
  @Path("/like/{postId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(
        responseCode = "400",
        description = "Bad user / Cannot like an already liked post"),
    @APIResponse(responseCode = "403", description = "Cannot like a blocked user post"),
    @APIResponse(responseCode = "404", description = "Post/User does not exist")
  })
  public Response postLikePostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Unlike a post.
   *
   * @param userId the ID of the user unliking the post
   * @param postId the ID of the post to be unliked
   * @return Response indicating the result of the operation
   */
  @DELETE
  @Path("/like/{postId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(
        responseCode = "400",
        description = "Bad user / Cannot unlike an already unliked post"),
    @APIResponse(responseCode = "403", description = "Cannot unlike a blocked post user"),
    @APIResponse(responseCode = "404", description = "Post/User does not exist")
  })
  public Response deleteLikePostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Follow a user.
   *
   * @param userId the ID of the user following
   * @param targetUserId the ID of the user to be followed
   * @return Response indicating the result of the operation
   */
  @POST
  @Path("/follow/{targetUserId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(
        responseCode = "400",
        description = "Bad User Id / Cannot follow an already followed user"),
    @APIResponse(responseCode = "403", description = "Cannot follow an blocked user"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response postFollowTargetList(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Unfollow a user.
   *
   * @param userId the ID of the user unfollowing
   * @param targetUserId the ID of the user to be unfollowed
   * @return Response indicating the result of the operation
   */
  @DELETE
  @Path("/follow/{targetUserId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(
        responseCode = "400",
        description = "Bad User Id / Cannot unfollow an unfollowed user"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response deleteFollowTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Block a user.
   *
   * @param userId the ID of the user blocking
   * @param targetUserId the ID of the user to be blocked
   * @return Response indicating the result of the operation
   */
  @POST
  @Path("/block/{targetUserId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user ID / Bad post format"),
    @APIResponse(responseCode = "403", description = "Target user already blocked"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response postBlockTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Unblock a user.
   *
   * @param userId the ID of the user unblocking
   * @param targetUserId the ID of the user to be unblocked
   * @return Response indicating the result of the operation
   */
  @DELETE
  @Path("/block/{targetUserId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user ID / Bad delete format"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response deleteBlockTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Get the list of userID who liked a post.
   *
   * @param userId the ID of the user requesting the list
   * @param targetUserId the ID of the user whose post likers are being requested
   * @return List of userIDs who liked the post
   */
  @GET
  @Path("/posts/{targetPostId}/likers")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user ID / Bad get format"),
    @APIResponse(responseCode = "403", description = "Target is a blocked user"),
    @APIResponse(responseCode = "404", description = "Post/Target user does not exist")
  })
  public Response getTargetLikersEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetPostId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Get the list of postId liked by a user.
   *
   * @param userId the ID of the user requesting the list
   * @param targetUserId the ID of the user whose liked posts are being requested
   * @return List of postIDs liked by the user
   */
  @GET
  @Path("/users/{targetUserId}/liked_posts")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user ID / Bad get format"),
    @APIResponse(responseCode = "403", description = "Target is a blocked user"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response getTargetLikedPostsEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Get the list of usersId followed by a user.
   *
   * @param userId the ID of the user requesting the list
   * @param targetUserId the ID of the user whose follows are being requested
   * @return List of usersId followed by a user
   */
  @GET
  @Path("/users/{targetUserId}/follows")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user ID / Bad get format"),
    @APIResponse(responseCode = "403", description = "Target is a blocked user"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response getTargetFollowsEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Get the list of followers of a user.
   *
   * @param userId the ID of the user requesting the list
   * @param targetUserId the ID of the user whose followers are being requested
   * @return List of usersId following the user
   */
  @GET
  @Path("/users/{targetUserId}/followers")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user ID / Bad get format"),
    @APIResponse(responseCode = "403", description = "Target is a blocked user"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response getTargetFollowersEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Get the list of usersId blocked by a user.
   *
   * @param userId the ID of the user requesting the list
   * @param targetUserId the ID of the user whose block list is being requested
   * @return List of usersId blocked by a user
   */
  @GET
  @Path("/users/{targetUserId}/block_list")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user ID / Bad get format"),
    @APIResponse(responseCode = "403", description = "Target is a blocked user"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response getTargetBlockListEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  /**
   * Get the list of users who blocked a user.
   *
   * @param userId the ID of the user requesting the list
   * @param targetUserId the ID of the user who is blocked by others
   * @return List of usersId who blocked the user
   */
  @GET
  @Path("/users/{targetUserId}/blocked_by")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user ID / Bad get format"),
    @APIResponse(responseCode = "403", description = "Target is a blocked user"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist")
  })
  public Response getTargetBlockByEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }
}
