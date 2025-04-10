package com.tinyx.search.controller;

import com.tinyx.search.service.SearchService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/search")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SearchController {

  @Inject SearchService searchService;

  /**
   * Searches for posts based on a text phrase and/or a list of hashtags.
   *
   * @param userId The UUID of the user performing the search, from the X-User header. Used to
   *     filter out posts authored by cloked users
   * @param phrase The text phrase to search for in post content (optional).
   * @param hashtags List of hashtags to filter posts by (optional).
   */
  @Path("/posts")
  @GET
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "Bad userId UUID"),
    @APIResponse(responseCode = "400", description = "No queries (phrase and hashtags are empty"),
    @APIResponse(
        responseCode = "503",
        description = "If ElasticSearch is unreachable/search error"),
    @APIResponse(
        responseCode = "503",
        description = "If srvc-post is unreachable to get PostContract")
  })
  public Response searchPost(
      @HeaderParam("X-User") UUID userId,
      @QueryParam("phrase") String phrase,
      @QueryParam("hashtags") List<String> hashtags) {
    return Response.ok(searchService.searchPost(userId, phrase, hashtags)).build();
  }
}
