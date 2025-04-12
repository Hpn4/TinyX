package com.tinyx.user.converter;

import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserConverter {
  public UserEntity convertUser(UserContract contract) {
    return new UserEntity(
        contract.id,
        contract.userName,
        contract.creationDate,
        contract.blockedUsers,
        contract.posts);
  }

  public UserContract convertUser(UserEntity entity) {
    return new UserContract(
        entity.id, entity.userName, entity.creationDate, entity.blockedUsers, entity.posts);
  }
}
