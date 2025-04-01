package com.tinyx.user;

import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserConverter {
  UserEntity convertUser(UserContract contract) {
    return new UserEntity(contract.id, contract.userName, contract.creationDate);
  }

  UserContract convertUser(UserEntity entity) {
    return new UserContract(entity.id, entity.userName, entity.creationDate);
  }
}
