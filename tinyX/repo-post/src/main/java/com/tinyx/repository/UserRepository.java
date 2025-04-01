package com.tinyx.repository;

import com.tinyx.user.entity.UserEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<UserEntity, UUID> {
  /**
   * Creates a user in the DB If a user with this ID already exists, an error is sent through REDIS.
   *
   * @param user The user to add to the DB.
   */
  public void createUser(UserEntity user) {}

  /**
   * Attemps to update user data (except its id) If no user matches the given id, no new user will
   * be created.
   *
   * @param user The new data to apply.
   */
  public void updateUser(UserEntity user) {}
}
