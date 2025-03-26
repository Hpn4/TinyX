package com.tinyx.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

/**
 * Handles all the endpoints that are purely post related. Maybe a new endpoint could be later added
 * to update user data (c.f. Miro)
 */
@Path("/posts")
public class PostController {
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
  public Response newPostEndpoint(@HeaderParam("X-User") UUID userId) // TODO add post as body
      {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }

  @DELETE
  @Path("/delete")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad user"),
    @APIResponse(responseCode = "403", description = "Cannot delete another user's post"),
    @APIResponse(responseCode = "404", description = "User / post does not exist")
  })
  public Response deletePostEndpoint(@HeaderParam("X-User") UUID userId) // TODO add postId as body
      {
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
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
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
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
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
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
    return Response.status(Response.Status.NOT_IMPLEMENTED).build();
  }
}
