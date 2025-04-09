package com.tinyx.clients;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "service-media-client")
public interface ServiceMediaRestClient {

  @GET
  @Path("/media/exists/{mediaId}")
  public CompletionStage<Boolean> doesMediaExistEndpoint(@PathParam("mediaId") UUID mediaId);

  @GET
  @Path("/media/get/{mediaId}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public CompletionStage<InputStream> getMediaEndpoint(@PathParam("mediaId") UUID mediaId);
}
