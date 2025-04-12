package com.tinyx.controller;

import com.tinyx.controller.request.CreatePostRequest;
import com.tinyx.service.PostService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

/**
 * Handles all the endpoints that are purely post related. Maybe a new endpoint could be later added
 * to update user data (c.f. Miro)
 */
@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostController {

  @Inject private PostService postService;

  @POST
  @Path("/new")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(
        responseCode = "400",
        description =
            "Bad user / post format, too many medias, post text exceeds 160 characters, cannot be reply and repost"),
    @APIResponse(responseCode = "403", description = "Cannot reply to blocked user"),
    @APIResponse(responseCode = "404", description = "User does not exist, reply target not found")
  })
  public Response newPostEndpoint(@HeaderParam("X-User") UUID userId, CreatePostRequest post) {
    postService.newPost(userId, post);
    return Response.ok().build();
  }

  @DELETE
  @Path("/delete/{postId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user"),
    @APIResponse(responseCode = "403", description = "Cannot delete another user's post"),
    @APIResponse(responseCode = "404", description = "User / post does not exist")
  })
  public Response deletePostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId) {
    postService.deletePost(userId, postId);
    return Response.ok().build();
  }

  /**
   * Uses a list of post IDs to fetch and return the corresponding post entities.
   *
   * @param userId The user querying the endpoint
   * @param postIds The list of post IDs to fetch
   * @return The corresponding posts extracted from mongoDB
   */
  @GET
  @Path("/posts")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad post ID inside of the list"),
    @APIResponse(responseCode = "404", description = "A post ID does not link to an existing post")
  })
  public Response queryPostsList(@HeaderParam("X-User") UUID userId, List<UUID> postIds) {
    return Response.ok(postService.getAllPost(postIds, userId)).build();
  }

  @GET
  @Path("/posts/{authorId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad author format"),
    @APIResponse(responseCode = "403", description = "Cannot get blocked user posts"),
    @APIResponse(responseCode = "404", description = "Author does not exist")
  })
  public Response queryUserPostsEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("authorId") UUID authorId) {
    return Response.ok(postService.getAllPostsFromUser(authorId, userId)).build();
  }

  @GET
  @Path("/post/{postId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad post format"),
    @APIResponse(responseCode = "403", description = "Post belongs to blocked user"),
    @APIResponse(responseCode = "404", description = "Post does not exist")
  })
  public Response queryPostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId) {
    return Response.ok(postService.getPostById(postId, userId)).build();
  }

  @GET
  @Path("/replies/{postId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad post format"),
    @APIResponse(responseCode = "403", description = "Post belongs to blocked user"),
    @APIResponse(responseCode = "404", description = "Post does not exist")
  })
  public Response queryPostRepliesEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId) {
    return Response.ok(postService.getRepliesByPostId(postId, userId)).build();
  }
}
