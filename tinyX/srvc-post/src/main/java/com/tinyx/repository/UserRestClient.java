package com.tinyx.repository;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "user-client")
@Path("/user")
public interface UserRestClient {
  @GET
  @Path("/get/id/{userId}")
  Response getUserById(@PathParam("userId") UUID userId);
}
