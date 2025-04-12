package com.tinyx.user.converter;

import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEntityToLightContractConverter {

  public LightUserContract convert(UserEntity entity) {
    return new LightUserContract(entity.id, entity.userName, entity.creationDate);
  }
}
