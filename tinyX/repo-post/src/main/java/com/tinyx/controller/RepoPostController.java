package com.tinyx.controller;

import com.tinyx.controller.contract.Post;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.List;
import java.util.UUID;

public class RepoPostController {
    @GET
    @Path("/posts/validate")
    public Response validateNewPost(Post post)
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/posts/posts")
    public Response queryPostsList(List<UUID> postIds) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/posts/posts/{authorId}")
    public Response queryUserPosts(@PathParam("authorId") UUID authorId)
    {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/posts/post/{postId}")
    public Response queryPost(@PathParam("postId") UUID postId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}