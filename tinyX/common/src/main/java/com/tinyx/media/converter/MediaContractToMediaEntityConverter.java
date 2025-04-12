package com.tinyx.media.converter;

import com.tinyx.media.contracts.MediaContract;
import com.tinyx.media.entity.MediaEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MediaContractToMediaEntityConverter {
  public MediaEntity convert(MediaContract contract) {
    if (contract == null) return null;
    return new MediaEntity(contract.id, contract.data);
  }
}
