package com.tinyx.repository;

import com.tinyx.user.contracts.UserContract;
import jakarta.ws.rs.*;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "user-client")
@Path("/user")
public interface UserRestClient {

  /**
   * Convert a list of users by their UUIDs.
   *
   * @param usersId A list of UUIDs to convert.
   * @return UserContract corresponding to the provided UUIDs.
   */
  @POST
  @Path("/get/id")
  List<UserContract> getUsersByIds(List<UUID> usersId);
}
