package com.tinyx.clients;

import com.tinyx.post.contracts.PostContract;
import jakarta.ws.rs.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "user-timeline-client")
@Path("/timeline")
public interface UserTimelineRestClient {
  @Path("/user")
  @GET
  public CompletionStage<List<PostContract>> getUserTimeline(@HeaderParam("X-User") UUID userId);

  @Path("/users")
  @GET
  public CompletionStage<List<PostContract>> getUsersTimeline(
      @HeaderParam("X-User") UUID userId, List<UUID> usersId);
}
