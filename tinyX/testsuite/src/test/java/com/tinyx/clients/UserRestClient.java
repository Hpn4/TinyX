package com.tinyx.clients;

import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.contracts.UserContract;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "user-client")
@Path("/user")
public interface UserRestClient {

  @POST
  @Path("/create/{userName}")
  public CompletionStage<Response> createUser(@PathParam("userName") String userName);

  @GET
  @Path("/get/username/{userName}")
  public CompletionStage<LightUserContract> getUserByName(@PathParam("userName") String userName);

  @GET
  @Path("/get/id/{userId}")
  public CompletionStage<UserContract> getUserById(@PathParam("userId") UUID userId);

  @GET
  @Path("/get/id")
  public CompletionStage<List<UserContract>> getUsersByIds(
      @QueryParam("usersId") List<UUID> usersId);
}
