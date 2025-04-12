package com.tinyx.controller;

import com.tinyx.ErrorCodes;
import com.tinyx.service.RelationsCommandService;
import com.tinyx.service.RelationsQueryService;
import jakarta.inject.Inject;
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

  @Inject RelationsCommandService commandService;

  @Inject RelationsQueryService queryService;

  /**
   * Like a post.
   *
   * @param userId the ID of the user liking the post
   * @param postId the ID of the post to be liked
   * @return Response indicating the result of the operation (Number of likes)
   */
  @POST
  @Path("/like/{postId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user"),
    @APIResponse(responseCode = "403", description = "Cannot like a blocked user post"),
    @APIResponse(responseCode = "409", description = "Post already liked"),
    @APIResponse(responseCode = "404", description = "Post/User does not exist"),
    @APIResponse(responseCode = "409", description = "Cannot like an owned post")
  })
  public Response postLikePostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId) {
    if (userId == null) ErrorCodes.WRONG_UUID.throwError("userId");

    return Response.ok(commandService.likePost(userId, postId)).build();
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
    @APIResponse(responseCode = "400", description = "Bad user"),
    @APIResponse(responseCode = "403", description = "Cannot unlike a blocked post user"),
    @APIResponse(responseCode = "204", description = "Post is not liked"),
    @APIResponse(responseCode = "404", description = "Post/User does not exist"),
    @APIResponse(responseCode = "409", description = "Cannot unlike an owned post")
  })
  public Response deleteLikePostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();

    return Response.ok(commandService.unlikePost(userId, postId)).build();
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
    @APIResponse(responseCode = "400", description = "Bad User Id"),
    @APIResponse(responseCode = "409", description = "Users already followed"),
    @APIResponse(responseCode = "403", description = "Cannot follow an blocked user"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist"),
    @APIResponse(responseCode = "409", description = "Cannot follow ourself")
  })
  public Response postFollowTargetList(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();

    commandService.followUser(userId, targetUserId);
    return Response.ok().build();
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
    @APIResponse(responseCode = "400", description = "Bad User Id"),
    @APIResponse(responseCode = "204", description = "User is not followed"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist"),
    @APIResponse(responseCode = "409", description = "Cannot unfollow ourself")
  })
  public Response deleteFollowTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();

    commandService.unfollowUser(userId, targetUserId);
    return Response.ok().build();
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
    @APIResponse(responseCode = "409", description = "Target user already blocked"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist"),
    @APIResponse(responseCode = "409", description = "Cannot block ourself")
  })
  public Response postBlockTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();

    commandService.blockUser(userId, targetUserId);
    return Response.ok().build();
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
    @APIResponse(responseCode = "204", description = "No block relations"),
    @APIResponse(responseCode = "400", description = "Bad user ID"),
    @APIResponse(responseCode = "404", description = "Target/User does not exist"),
    @APIResponse(responseCode = "409", description = "Cannot unblock ourself")
  })
  public Response deleteBlockTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();

    commandService.unblockUser(userId, targetUserId);
    return Response.ok().build();
  }

  /**
   * Get the list of userID who liked a post.
   *
   * @param userId the ID of the user requesting the list
   * @param targetPostId the ID of the user whose post likers are being requested
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
      @HeaderParam("X-User") UUID userId, @PathParam("targetPostId") UUID targetPostId) {

    return Response.ok(queryService.getLikers(targetPostId, userId)).build();
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

    return Response.ok(queryService.getLikedPost(targetUserId, userId)).build();
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

    return Response.ok(queryService.getUserFollows(targetUserId, userId)).build();
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

    return Response.ok(queryService.getFollowers(targetUserId, userId)).build();
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

    return Response.ok(queryService.getBlockedUsers(targetUserId, userId)).build();
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

    return Response.ok(queryService.getTargetBlock(targetUserId, userId)).build();
  }
}
