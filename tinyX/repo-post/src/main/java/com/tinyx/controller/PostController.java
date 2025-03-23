package com.tinyx.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.UUID;

/**
 * Handles all the endpoints that are purely post related.
 * Maybe a new endpoint could be later added to update user data (c.f. Miro)
 */
@Path("/posts")
public class PostController {
    @POST
    @Path("/new")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK"),
            @APIResponse(responseCode = "400", description = "Bad user / post format"),
            @APIResponse(responseCode = "404", description = "User does not exist")
    })
    public Response newPostEndpoint(@HeaderParam("X-User") UUID userId) // TODO add post as body
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/delete")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK"),
            @APIResponse(responseCode = "400", description = "Bad user"),
            @APIResponse(responseCode = "401", description = "Cannot delete another user's post"),
            @APIResponse(responseCode = "404", description = "User / post does not exist")
    })
    public Response deletePostEndpoint(@HeaderParam("X-User") UUID userId) // TODO add postId as body
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/posts/{authorId}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK"),
            @APIResponse(responseCode = "400", description = "Bad author format"),
            @APIResponse(responseCode = "404", description = "Author does not exist")
    })
    public Response queryUserPostsEndpoint(@PathParam("authorId") UUID authorId)
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/post/{postId}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK"),
            @APIResponse(responseCode = "400", description = "Bad post format"),
            @APIResponse(responseCode = "404", description = "Post does not exist")
    })
    public Response queryPostEndpoint(@PathParam("postId") UUID postId)
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/replies/{postId}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK"),
            @APIResponse(responseCode = "400", description = "Bad post format"),
            @APIResponse(responseCode = "404", description = "Post does not exist")
    })
    public Response queryPostRepliesEndpoint(@PathParam("postId") UUID postId)
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
