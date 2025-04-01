package com.tinyx.service;

import com.tinyx.repository.UserRepository;
import com.tinyx.user.UserConverter;
import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;
  @Inject UserConverter userConverter;

  public boolean isUserValid(UserContract userContract) {
    return !(userContract.id == null
        || userContract.userName == null
        || userContract.creationDate == null);
  }

  public void createUser(List<UserContract> users) {
    users.removeIf(user -> !isUserValid(user));
    List<UserEntity> userEntities =
        users.stream().map(userConverter::convertUser).collect(Collectors.toList());

    userRepository.createUser(userEntities);
  }

  public void updateUser(List<UserContract> users) {
    users.removeIf(user -> !isUserValid(user));
    List<UserEntity> userEntities =
        users.stream().map(userConverter::convertUser).collect(Collectors.toList());

    userRepository.updateUser(userEntities);
  }
}
