package com.tinyx.controller;

import com.tinyx.controller.contract.Media;
import com.tinyx.controller.contract.Post;
import com.tinyx.controller.contract.User;
import com.tinyx.service.MediaService;
import com.tinyx.service.PostService;
import com.tinyx.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

// No blocker user and other types of checks here, so it's as simple as possible to get information.
public class RepoPostController {
    @Inject PostService postService;
    @Inject UserService userService;
    @Inject MediaService mediaService;

    @Inject Logger logger;

    @GET
    @Path("/posts/posts")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns a list of posts matching the given IDs"),
            @APIResponse(responseCode = "400", description = "Bad post ID inside of the list"),
            @APIResponse(responseCode = "404", description = "A post ID does not link to an existing post")
    })
    public Response queryPostsList(List<UUID> postIds) {
        if (postIds == null || postIds.stream().anyMatch(Objects::isNull)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("ID list or one of its IDs is null").build();
        }

        List<Post> posts = postService.queryPostsList(postIds);

        if (posts == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No posts match an ID").build();
        }

        return Response.ok(posts).build();
    }

    @GET
    @Path("/posts/posts/{authorId}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns the posts authored by the given ID"),
            @APIResponse(responseCode = "400", description = "Bad author ID"),
            @APIResponse(responseCode = "404", description = "Author does not exist")
    })
    public Response queryUserPosts(@PathParam("authorId") UUID authorId)
    {
        if (authorId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Author ID is null").build();
        }

        List<Post> posts = postService.queryUserPosts(authorId);

        if (posts == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No posts match the given authordId: " + authorId.toString()).build();
        }

        return Response.ok(posts).build();
    }

    @GET
    @Path("/posts/post/{postId}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns a single post matching the ID"),
            @APIResponse(responseCode = "400", description = "Bad post ID"),
            @APIResponse(responseCode = "404", description = "Post does not exist")
    })
    public Response queryPost(@PathParam("postId") UUID postId) {
        if (postId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Post ID is null").build();
        }

        Post post = postService.querySpecificPost(postId);

        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No post matches the given ID").build();
        }

        return Response.ok(post).build();
    }

    @GET
    @Path("/user/{userId}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns the user matching the ID"),
            @APIResponse(responseCode = "400", description = "Bad user ID"),
            @APIResponse(responseCode = "404", description = "User does not exist")
    })
    public Response queryUser(@PathParam("userId") UUID userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User ID is null").build();
        }

        User user = userService.queryUser(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No user matches the given ID").build();
        }

        return Response.ok(user).build();
    }

    @GET
    @Path("/media/{mediaId}")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK, returns the media matching the ID"),
            @APIResponse(responseCode = "400", description = "Bad media ID"),
            @APIResponse(responseCode = "404", description = "Media does not exist")
    })
    public Response queryMedia(@PathParam("mediaId") UUID mediaId) {
        if (mediaId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Media ID is null").build();
        }

        Media media = mediaService.getMedia(mediaId);

        if (media == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No media matches the given ID").build();
        }

        return Response.ok(media).build();
    }
}