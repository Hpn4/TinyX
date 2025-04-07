package com.tinyx.media;

import com.tinyx.media.contracts.MediaContract;
import com.tinyx.media.entity.MediaEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MediaConverter {
  public MediaEntity convertMedia(MediaContract contract) {
    if (contract == null) return null;
    return new MediaEntity(contract.id, contract.data);
  }

  public MediaContract convertMedia(MediaEntity entity) {
    if (entity == null) return null;
    return new MediaContract(entity.id, entity.data);
  }
}
