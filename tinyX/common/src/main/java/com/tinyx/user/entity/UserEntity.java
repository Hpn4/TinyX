package com.tinyx.user.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(collection = "Users")
public class UserEntity {

  @BsonId public UUID id;
  public String userName;
  public ZonedDateTime creationDate;
  public List<UUID> blockedUsers;

  public UserEntity(UUID id, String userName, ZonedDateTime creationDate) {

    this.id = id;
    this.userName = userName;
    this.creationDate = creationDate;
    this.blockedUsers = new ArrayList<>();
  }
}
