package com.tinyx.service;

import com.tinyx.controller.contract.User;
import com.tinyx.repository.UserRepository;
import jakarta.inject.Inject;

import java.util.UUID;

public class UserService {

    @Inject
    UserRepository userRepository;

    public void createUser(User user) {}

    public User getUser(UUID id) {
        return null;
    }

    public void updateUser(User user) {}
}
