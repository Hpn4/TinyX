package com.tinyx.repository;

import com.tinyx.post.contracts.PostContract;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Rest client for contacting the UserTimeline service. Used for getting a specific user's timeline.
 */
@RegisterRestClient(baseUri = "http://localhost:8081")
public interface UserTimelineRestClient {

  @Path("/timeline/users")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  List<PostContract> GetUsersTimeline(List<UUID> userIds);
}
