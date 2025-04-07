package com.tinyx.user;

import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEntityToLightContractConverter {
  public UserEntity convertUser(LightUserContract contract) {
    return new UserEntity(contract.id, contract.userName, contract.creationDate);
  }

  public LightUserContract convertUser(UserEntity entity) {
    return new LightUserContract(entity.id, entity.userName, entity.creationDate);
  }
}
