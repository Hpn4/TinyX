package com.tinyx.clients;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.requests.CreatePostRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "post-client")
@Path("/posts")
public interface PostRestClient {
  @POST
  @Path("/new")
  public CompletionStage<Response> newPostEndpoint(
      @HeaderParam("X-User") UUID userId, CreatePostRequest post);

  @DELETE
  @Path("/delete/{postId}")
  public CompletionStage<Response> deletePostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId);

  @GET
  @Path("/posts")
  public CompletionStage<List<PostContract>> queryPostsList(
      @HeaderParam("X-User") UUID userId, List<UUID> postIds);

  @GET
  @Path("/posts/{authorId}")
  public CompletionStage<List<PostContract>> queryUserPostsEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("authorId") UUID authorId);

  @GET
  @Path("/post/{postId}")
  public CompletionStage<PostContract> queryPostEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId);

  @GET
  @Path("/replies/{postId}")
  public CompletionStage<List<PostContract>> queryPostRepliesEndpoint(
      @HeaderParam("X-User") UUID userId, @PathParam("postId") UUID postId);
}
