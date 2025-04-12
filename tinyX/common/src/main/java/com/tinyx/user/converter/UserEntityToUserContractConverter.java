package com.tinyx.user.converter;

import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEntityToUserContractConverter {

  public UserContract convert(UserEntity entity) {
    return new UserContract(
        entity.id, entity.userName, entity.creationDate, entity.blockedUsers, entity.posts);
  }
}
