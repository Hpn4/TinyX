package com.tinyx.controller;

import com.tinyx.ErrorCodes;
import com.tinyx.media.contracts.MediaContract;
import com.tinyx.service.MediaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestResponse;

/** Handles the READ operations related to medias. */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MediaController {
  @Inject MediaService mediaService;

  /**
   * Looks for a media in the database and returns whether the operation succeeded or not.
   *
   * @param mediaId The id of the media to look for.
   * @return True if the media exists, False if not.
   */
  @GET
  @Path("/media/exists/{mediaId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(
        responseCode = "400",
        description = "Bad media Id, either null or badly formatted"),
  })
  public Boolean doesMediaExistEndpoint(@PathParam("mediaId") UUID mediaId) {
    if (mediaId == null) ErrorCodes.MEDIA_BAD_ID.throwError();

    return mediaService.doesMediaExist(mediaId);
  }

  @GET
  @Path("/media/get/{mediaId}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(
        responseCode = "400",
        description = "Bad media Id, either null or badly formatted"),
    @APIResponse(responseCode = "404", description = "Media does not exist")
  })
  public RestResponse<InputStream> getMediaEndpoint(@PathParam("mediaId") UUID mediaId) {
    if (mediaId == null) ErrorCodes.MEDIA_BAD_ID.throwError();

    MediaContract media = mediaService.getMedia(mediaId);

    if (media == null) ErrorCodes.MEDIA_NOT_FOUND.throwError(mediaId);

    return RestResponse.ok(media.data);
  }
}
