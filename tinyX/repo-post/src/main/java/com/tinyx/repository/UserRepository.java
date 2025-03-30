package com.tinyx.repository;

import com.tinyx.repository.entity.User;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<User, UUID> {
  /**
   * Creates a user in the DB If a user with this ID already exists, an error is sent through REDIS.
   *
   * @param user The user to add to the DB.
   */
  public void createUser(User user) {}

  /**
   * Queries a user by its UUID Finding a user by something other than its id should not be handled
   * here (but in Neo4j)
   *
   * @param id The ID of the user to get
   * @return The queried user. May be null if none if found
   */
  public User getUser(UUID id) {
    return null;
  }

  /**
   * Attemps to update user data (except its id) If no user matches the given id, no new user will
   * be created.
   *
   * @param user The new data to apply.
   */
  public void updateUser(User user) {}
}
