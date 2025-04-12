package com.tinyx.clients;

import com.tinyx.post.contracts.PostContract;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "search-client")
@Path("/search")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SearchRestClient {
  @Path("/posts")
  @GET
  public CompletionStage<List<PostContract>> searchPost(
      @HeaderParam("X-User") UUID userId,
      @QueryParam("phrase") String phrase,
      @QueryParam("hashtags") List<String> hashtags);
}
