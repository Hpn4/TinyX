package com.tinyx.service;

import com.tinyx.repository.UserRepository;
import com.tinyx.user.contracts.UserContract;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;

  public void createUser(UserContract user) {}

  public void updateUser(UserContract user) {}
}
