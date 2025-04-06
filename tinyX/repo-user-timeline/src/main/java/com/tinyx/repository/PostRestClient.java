package com.tinyx.repository;

import com.tinyx.post.contracts.PostContract;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** Rest client for contacting the Post service. Used for getting a specific user's posts. */
@RegisterRestClient(baseUri = "{tinyx.rest-client.srvc-post.host}")
public interface PostRestClient {

  @Path("/posts/{authorId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  List<PostContract> getUserPosts(
      @HeaderParam("X-User") UUID userId, @PathParam("authorId") UUID authorId);
}
