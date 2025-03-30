package com.tinyx.repository.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDate;
import java.util.UUID;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(collection = "Users")
public class User {

  @BsonId public UUID id;
  public String userName;
  public LocalDate creationDate;

  public User(UUID id, String userName, LocalDate creationDate) {

    this.id = id;
    this.userName = userName;
    this.creationDate = creationDate;
  }
}
