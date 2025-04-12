package com.tinyx.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.tinyx.user.entity.UserEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<UserEntity, UUID> {
  @ConfigProperty(name = "tinyx.srvc-user.collection")
  String usersCollectionName;

  /**
   * Set up the colection by defining the userName field as unique so as not to have several users
   * with the same name
   */
  @PostConstruct
  void init() {
    MongoCollection<Document> collection = mongoDatabase().getCollection(usersCollectionName);

    IndexOptions options = new IndexOptions().unique(true);
    collection.createIndex(Indexes.ascending("userName"), options);
  }

  /**
   * Finds a user by their username.
   *
   * @param userName The username of the user to be found.
   * @return Entity of user found (empty if not found).
   */
  public Optional<UserEntity> findByName(String userName) {
    return find("userName = ?1", userName).firstResultOptional();
  }
}
