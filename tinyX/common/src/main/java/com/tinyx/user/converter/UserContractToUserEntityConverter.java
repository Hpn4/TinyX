package com.tinyx.user.converter;

import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserContractToUserEntityConverter {

  /**
   * Converts a UserContract to a UserEntity.
   *
   * @param contract The UserContract to be converted.
   * @return A UserEntity that corresponds to the given UserContract.
   */
  public UserEntity convert(UserContract contract) {
    return new UserEntity(
        contract.id,
        contract.userName,
        contract.creationDate,
        contract.blockedUsers,
        contract.posts);
  }
}
