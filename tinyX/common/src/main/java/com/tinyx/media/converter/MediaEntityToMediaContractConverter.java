package com.tinyx.media.converter;

import com.tinyx.media.contracts.MediaContract;
import com.tinyx.media.entity.MediaEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MediaEntityToMediaContractConverter {
  public MediaContract convert(MediaEntity entity) {
    if (entity == null) return null;
    return new MediaContract(entity.id, entity.data);
  }
}
