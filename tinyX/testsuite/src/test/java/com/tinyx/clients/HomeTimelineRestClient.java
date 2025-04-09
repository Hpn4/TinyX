package com.tinyx.clients;

import com.tinyx.post.contracts.PostContract;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "home-timeline-client")
@Path("/timeline")
public interface HomeTimelineRestClient {
  @GET
  @Path("/home")
  public CompletionStage<List<PostContract>> getHomeTimeline(@HeaderParam("X-User") UUID userId);
}
