package com.tinyx.repository;

import com.tinyx.post.contracts.PostContract;
import jakarta.ws.rs.*;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST client for interacting with the Post service. The client uses the configured base URI from
 * the {@code post-client/mp-rest/url} property.
 */
@RegisterRestClient(configKey = "post-client")
@Path("/posts")
public interface PostRestClient {
  /**
   * Retrieves a list of posts, filtering out posts from users blocked by the specified user {@code
   * userId}.
   *
   * @param userId The UUID of the user used to filter out posts from blocked users.
   * @param postIds A list of post UUIDs to retrieve.
   * @return A list of {@link com.tinyx.post.contracts.PostContract} with posts from blocked users
   *     excluded.
   */
  @GET
  @Path("/posts")
  public List<PostContract> queryPostsList(@HeaderParam("X-User") UUID userId, List<UUID> postIds);
}
