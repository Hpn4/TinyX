package com.tinyx;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.function.Supplier;

public enum ErrorCodes {
  USER_NOT_FOUND(Status.NOT_FOUND, "User %s was not found"),
  USERS_NOT_FOUND(Status.NOT_FOUND, "One or multiple users were not found"),

  BLOCKED_USER(Status.FORBIDDEN, "User %s is a blocked relation"),
  ALREADY_BLOCKED_USER(Status.CONFLICT, "User %s is already blocked"),
  NO_BLOCKED_USER(Status.NO_CONTENT, "User %s is not blocked"),

  ALREADY_FOLLOWED_USER(Status.CONFLICT, "User %s is already followed"),
  NO_FOLLOWED_USER(Status.NO_CONTENT, "User %s is not followed"),

  ALREADY_LIKED_POST(Status.CONFLICT, "User %s has already liked post %s"),
  NO_LIKE(Status.NO_CONTENT, "User %s has not liked post %s"),

  DUPLICATE_KEY(Status.CONFLICT, "A user with the username %s already exists"),
  BAD_POST_FORMAT(Status.BAD_REQUEST, "The post format is invalid"),
  WRONG_UUID(Status.BAD_REQUEST, "One UUID %s is null or ill formated"),
  BLOCKED_USER_POST(Status.FORBIDDEN, "User %s is blocked"),
  FORBIDDEN_USER_ACTION(Status.FORBIDDEN, "The user %s cannot do this action"),
  POST_NOT_FOUND(Status.NOT_FOUND, "Post %s was not found"),
  MEDIA_NOT_FOUND(Status.NOT_FOUND, "Media %s was not found"),
  MEDIA_BAD_ID(Status.BAD_REQUEST, "Null media ID"),
  UNREACHABLE(Status.SERVICE_UNAVAILABLE, "%s is unreachable"),
  MEDIA_BAD_STREAM(Status.BAD_REQUEST, "Media stream could not be resolved");

  private final Status status;
  private final String errorMessage;

  ErrorCodes(Status status, String errorMessage) {
    this.status = status;
    this.errorMessage = errorMessage;
  }

  public void throwError(Object... args) {
    throw asSupplier(args).get();
  }

  public Supplier<WebApplicationException> asSupplier(Object... args) {
    return () ->
        new WebApplicationException(
            Response.status(status).entity(String.format(errorMessage, args)).build());
  }
}
