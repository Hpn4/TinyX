package com.tinyx.media.contracts;

import java.io.InputStream;
import java.util.UUID;

public class MediaContract {
  public UUID id;
  public InputStream data;

  public MediaContract(UUID id, InputStream data) {
    this.id = id;
    this.data = data;
  }
}
