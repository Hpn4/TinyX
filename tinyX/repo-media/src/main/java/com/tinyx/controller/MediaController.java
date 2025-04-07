package com.tinyx.controller;

import com.tinyx.ErrorCodes;
import com.tinyx.media.contracts.MediaContract;
import com.tinyx.service.MediaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

/** Handles the WRITE operations related to medias. */
@Path("/")
public class MediaController {
  @Inject MediaService mediaService;

  /**
   * The endpoint for uploading a media. If everything succeeds, the endpoint will return
   * immediately, while the media insertion will be done asynchronously.
   *
   * @param mediaStream The media to upload to the database. The REST dependencies handle
   *     InputStreams well, it even works with SwaggerUI.
   * @return The generated ID for the uploaded media. Save it, it won't be accessible again!
   */
  @POST
  @Path("/media/upload")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(schema = @Schema(implementation = UUID.class))),
    @APIResponse(responseCode = "400", description = "Bad media stream"),
  })
  public UUID uploadMediaEndpoint(InputStream mediaStream) {
    if (mediaStream == null) ErrorCodes.MEDIA_BAD_STREAM.throwError();

    UUID id = UUID.randomUUID();
    mediaService.uploadMedia(new MediaContract(id, mediaStream));

    return id;
  }
}
