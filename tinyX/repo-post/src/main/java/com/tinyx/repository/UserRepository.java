package com.tinyx.repository;

import com.tinyx.user.entity.UserEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<UserEntity, UUID> {
  /**
   * Creates a user in the DB If a user with this ID already exists, an error is sent through REDIS.
   *
   * @param users The user to add to the DB.
   */
  public void createUser(List<UserEntity> users) {
    persist(users);
  }

  /**
   * Attemps to update user data (except its id) If no user matches the given id, no new user will
   * be created.
   *
   * @param users The new data to apply.
   */
  public void updateUser(List<UserEntity> users) {
    update(users);
  }
}
