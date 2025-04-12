package com.tinyx.controller;

import com.tinyx.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

  @Inject UserService userService;

  /**
   * Create a new user from a nickname if one isn't already taken
   *
   * @param userName username of the new account.
   * @return Whether it succeeds in creating the user
   */
  @POST
  @Path("/create/{userName}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "incorrectly formatted user name"),
    @APIResponse(responseCode = "409", description = "user with same name already exists"),
    @APIResponse(responseCode = "500", description = "Internal error")
  })
  public Response createUser(@PathParam("userName") String userName) {
    if (userName.isBlank()) return Response.status(Response.Status.BAD_REQUEST).build();
    return Response.ok(userService.createUser(userName)).build();
  }

  /**
   * Retrieve a user account
   *
   * @param userName username of the account.
   * @return The account, if previously created
   */
  @GET
  @Path("/get/username/{userName}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "incorrectly formatted username"),
    @APIResponse(responseCode = "404", description = "user does not exists"),
    @APIResponse(responseCode = "500", description = "Internal error")
  })
  public Response getUserByName(@PathParam("userName") String userName) {
    if (userName == null || userName.isBlank())
      return Response.status(Response.Status.BAD_REQUEST).build();
    return Response.ok(userService.getUserByName(userName)).build();
  }

  /**
   * Retrieve a user account
   *
   * @param userId ID of the account.
   * @return The account, if previously created
   */
  @GET
  @Path("/get/id/{userId}")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "incorrectly formatted user Id"),
    @APIResponse(responseCode = "404", description = "user does not exists"),
    @APIResponse(responseCode = "500", description = "Internal error")
  })
  public Response getUserById(@PathParam("userId") UUID userId) {
    if (userId == null) return Response.status(Response.Status.BAD_REQUEST).build();
    return Response.ok(userService.getUserById(userId)).build();
  }

  /**
   * Retrieve multiple user accounts
   *
   * @param usersId IDs of users.
   * @return accounts, if previously created
   */
  @POST
  @Path("/get/id")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "OK"),
    @APIResponse(responseCode = "400", description = "incorrectly formatted user's Id"),
    @APIResponse(responseCode = "404", description = "users do not exists"),
    @APIResponse(responseCode = "500", description = "Internal error")
  })
  public Response getUsersByIds(List<UUID> usersId) {
    if (usersId == null || usersId.isEmpty())
      return Response.status(Response.Status.BAD_REQUEST).build();
    return Response.ok(userService.getUsersById(usersId)).build();
  }
}
