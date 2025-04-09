package com.tinyx.clients;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "repo-media-client")
public interface RepoMediaRestClient {

  @POST
  @Path("/media/upload")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  public CompletionStage<UUID> uploadMediaEndpoint(InputStream mediaStream);
}
