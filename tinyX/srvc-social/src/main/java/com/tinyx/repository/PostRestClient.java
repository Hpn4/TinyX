package com.tinyx.repository;

import com.tinyx.post.contracts.PostContract;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "post-client")
@Path("/posts")
public interface PostRestClient {

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
  List<PostContract> queryPostsList(@HeaderParam("X-User") UUID userId, List<UUID> postIds);
}
