package com.tinyx;

/**
 * Represents the possible operations that can be done on a user in the HomeTimeline and USER Mongo
 * collection.
 *
 * <p>HomeTimeline
 *
 * <p>Can be either ADD (adding UUIDs to the timeline) or DELETE (removing UUIDs from the timeline).
 *
 * <p>USER
 *
 * <p>Can be either ADD (adding UUIDs to the list containing blocked users) or DELETE (removing *
 * UUIDs from the list containing blocked users).
 */
public enum Operation {
  ADD,
  DELETE
}
