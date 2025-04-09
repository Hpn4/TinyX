package com.tinyx.repository;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "media-client")
@Path("/")
public interface MediaRestClient {
  @GET
  @Path("/media/exists/{mediaId}")
  Boolean doesMediaExistEndpoint(@PathParam("mediaId") UUID mediaId);
}
