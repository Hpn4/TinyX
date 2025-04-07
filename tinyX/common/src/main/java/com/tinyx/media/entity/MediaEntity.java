package com.tinyx.media.entity;

import java.io.InputStream;
import java.util.UUID;

/**
 * Represents all information related to a media stored in the database. The structure of this class
 * doesn't actually represent how the data is stored, but it regroups all the information that is.
 *
 * <p>In the database, medias are stored using GridFS, a mongo tool that helps dealing with big
 * files (such as medias!).
 */
public class MediaEntity {

  /** The ID of the media. In the database, it is a media's `filename` and NOT it's `ObjectId`. */
  public UUID id;

  /**
   * The data itself of the media. The `InputStream` type is handled well both by GridFS and by the
   * Rest services.
   */
  public InputStream data;

  public MediaEntity(UUID id, InputStream media) {
    this.id = id;
    this.data = media;
  }
}
