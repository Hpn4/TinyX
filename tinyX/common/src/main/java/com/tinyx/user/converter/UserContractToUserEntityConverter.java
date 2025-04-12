package com.tinyx.user.converter;

import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserContractToUserEntityConverter {

  public UserEntity convert(UserContract contract) {
    return new UserEntity(
        contract.id,
        contract.userName,
        contract.creationDate,
        contract.blockedUsers,
        contract.posts);
  }
}
