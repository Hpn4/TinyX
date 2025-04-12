package com.tinyx.user.converter;

import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LightUserContractToUserEntityConverter {

  public UserEntity convert(LightUserContract contract) {
    return new UserEntity(contract.id, contract.userName, contract.creationDate);
  }
}
