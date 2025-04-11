package com.tinyx.timeline;

/**
 * Represents the possible operations that can be done on a user in the HomeTimeline Mongo
 * collection.
 *
 * <p>Can be either ADD (adding UUIDs to the timeline) or DELETE (removing UUIDs from the timeline).
 */
public enum HomeTimelineOperation {
  ADD,
  DELETE
}
