package com.tinyx.user.converter;

import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEntityToLightContractConverter {

  /**
   * Converts a UserEntity to a LightUserContract.
   *
   * @param entity The UserEntity to be converted.
   * @return A LightUserContract that corresponds to the given UserEntity.
   */
  public LightUserContract convert(UserEntity entity) {
    return new LightUserContract(entity.id, entity.userName, entity.creationDate);
  }
}
