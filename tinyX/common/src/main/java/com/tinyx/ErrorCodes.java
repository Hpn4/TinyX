package com.tinyx;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.function.Supplier;

public enum ErrorCodes {
  USER_NOT_FOUND(Status.NOT_FOUND, "User %s was not found"),
  USERS_NOT_FOUND(Status.NOT_FOUND, "One or multiple users were not found"),
  MEDIA_BAD_ID(Status.BAD_REQUEST, "Null media ID"),
  MEDIA_BAD_STREAM(Status.BAD_REQUEST, "Media stream could not be resolved"),
  MEDIA_NOT_FOUND(Status.NOT_FOUND, "Media with ID '%s' was not found"),
  DUPLICATE_KEY(Status.CONFLICT, "A user with the username %s already exists");

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
