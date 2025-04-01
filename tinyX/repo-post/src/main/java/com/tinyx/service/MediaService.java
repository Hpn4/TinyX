package com.tinyx.service;

import com.tinyx.media.contracts.MediaContract;
import com.tinyx.repository.MediaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class MediaService {
  @Inject MediaRepository mediaRepository;

  public UUID uploadMedia(MediaContract media) {
    return null;
  }
}
