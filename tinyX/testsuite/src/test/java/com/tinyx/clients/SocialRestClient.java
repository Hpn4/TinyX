package com.tinyx.clients;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.user.contracts.LightUserContract;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/social")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "social-client")
public interface SocialRestClient {

  @POST
  @Path("/like/{postId}")
  public CompletionStage<Response> postLikePostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId);

  @DELETE
  @Path("/like/{postId}")
  public CompletionStage<Response> deleteLikePostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId);

  @POST
  @Path("/follow/{targetUserId}")
  public CompletionStage<Response> postFollowTargetList(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);

  @DELETE
  @Path("/follow/{targetUserId}")
  public CompletionStage<Response> deleteFollowTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);

  @POST
  @Path("/block/{targetUserId}")
  public CompletionStage<Response> postBlockTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);

  @DELETE
  @Path("/block/{targetUserId}")
  public CompletionStage<Response> deleteBlockTargetEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);

  @GET
  @Path("/posts/{targetPostId}/likers")
  public CompletionStage<List<LightUserContract>> getTargetLikersEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetPostId") UUID targetPostId);

  @GET
  @Path("/users/{targetUserId}/liked_posts")
  public CompletionStage<List<PostContract>> getTargetLikedPostsEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);

  @GET
  @Path("/users/{targetUserId}/follows")
  public CompletionStage<List<LightUserContract>> getTargetFollowsEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);

  @GET
  @Path("/users/{targetUserId}/followers")
  public CompletionStage<List<LightUserContract>> getTargetFollowersEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);

  @GET
  @Path("/users/{targetUserId}/block_list")
  public CompletionStage<List<LightUserContract>> getTargetBlockListEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);

  @GET
  @Path("/users/{targetUserId}/blocked_by")
  public CompletionStage<List<LightUserContract>> getTargetBlockByEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("targetUserId") UUID targetUserId);
}
