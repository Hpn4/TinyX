package com.tinyx.media.contracts;

import java.io.InputStream;
import java.util.UUID;

public class MediaContract {

  /**
   * Represents the UUID of a media. Will not be received when uploading a media, it is instead
   * generated internally.
   */
  public UUID id;

  /**
   * 'Contains' the file. This stream format is handled well both in the database with GridFS and
   * with the REST endpoints. It also avoids having to carry byte arrays everywhere.
   */
  public InputStream data;

  public MediaContract() {}

  /**
   * Constructs a new MediaContract from the specified ID and data stream.
   *
   * @param id The mediaID.
   * @param data The InputStream containing the media data.
   */
  public MediaContract(UUID id, InputStream data) {
    this.id = id;
    this.data = data;
  }
}
