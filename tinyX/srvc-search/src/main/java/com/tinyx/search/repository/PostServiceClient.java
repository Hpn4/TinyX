package com.tinyx.search.repository;

import com.tinyx.post.contracts.PostContract;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "post-client")
@Path("/posts")
public interface PostServiceClient {

  @GET
  @Path("/posts")
  List<PostContract> queryPostsList(@HeaderParam("X-User") UUID userId, List<UUID> postIds);
}
