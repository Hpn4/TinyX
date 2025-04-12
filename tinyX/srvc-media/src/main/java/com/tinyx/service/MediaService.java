package com.tinyx.service;

import com.tinyx.media.contracts.MediaContract;
import com.tinyx.media.converter.MediaEntityToMediaContractConverter;
import com.tinyx.media.entity.MediaEntity;
import com.tinyx.repository.MediaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class MediaService {
  @Inject MediaRepository mediaRepository;

  @Inject MediaEntityToMediaContractConverter mediaEntityToMediaContractConverter;

  /**
   * Fetches a media (and its data) and return it.
   *
   * @param mediaId The ID of the media to fetch.
   * @return A media contract containing all the needed info; null if no match is found.
   */
  public MediaContract getMedia(UUID mediaId) {
    MediaEntity media = mediaRepository.getMedia(mediaId);

    if (media == null) return null;

    return mediaEntityToMediaContractConverter.convert(media); // Handles the null (not found) case
  }

  /**
   * Checks whether a media exists in the database.
   *
   * @param mediaId The media ID to look for.
   * @return Whether the media has been located in the database.
   */
  public Boolean doesMediaExist(UUID mediaId) {
    return mediaRepository.exists(mediaId);
  }
}
