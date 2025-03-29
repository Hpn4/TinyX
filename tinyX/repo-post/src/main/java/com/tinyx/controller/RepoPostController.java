package com.tinyx.controller;

import com.tinyx.controller.contract.Post;
import com.tinyx.service.MediaService;
import com.tinyx.service.PostService;
import com.tinyx.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

// No blocker user and other types of checks here, so it's as simple as possible to get information.
public class RepoPostController {
    @Inject PostService postService;
    @Inject UserService userService;
    @Inject MediaService mediaService;

    @Inject Logger logger;

    @GET
    @Path("/posts/validate")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, the given post is fine to create"),
            @APIResponse(responseCode = "400", description = "KO, the given post cannot be created, check the body"),
    })
    public Response validateNewPost(Post post)
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns a list of posts matching the given IDs"),
            @APIResponse(responseCode = "400", description = "Bad post ID inside of the list"),
            @APIResponse(responseCode = "404", description = "A post ID does not link to an existing post")
    })
    @GET
    @Path("/posts/posts")
    public Response queryPostsList(List<UUID> postIds) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns the posts authored by the given ID"),
            @APIResponse(responseCode = "400", description = "Bad author ID"),
            @APIResponse(responseCode = "404", description = "Author does not exist")
    })
    @GET
    @Path("/posts/posts/{authorId}")
    public Response queryUserPosts(@PathParam("authorId") UUID authorId)
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/posts/post/{postId}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns a single post matching the ID"),
            @APIResponse(responseCode = "400", description = "Bad post ID"),
            @APIResponse(responseCode = "404", description = "Post does not exist")
    })
    public Response queryPost(@PathParam("postId") UUID postId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns the user matching the ID"),
            @APIResponse(responseCode = "400", description = "Bad user ID"),
            @APIResponse(responseCode = "404", description = "User does not exist")
    })
    @GET
    @Path("/user/{userId}")
    public Response queryUser(@PathParam("userId") UUID userId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns the media matching the ID"),
            @APIResponse(responseCode = "400", description = "Bad media ID"),
            @APIResponse(responseCode = "404", description = "Media does not exist")
    })
    @GET
    @Path("/media/{mediaId}")
    public Response queryMedia(@PathParam("mediaId") UUID mediaId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}