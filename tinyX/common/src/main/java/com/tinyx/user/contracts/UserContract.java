package com.tinyx.user.contracts;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserContract {
  public UUID id;
  public String userName;
  public ZonedDateTime creationDate;
  public List<UUID> blockedUsers;
  public List<UUID> posts;

  public UserContract() {}

  public UserContract(UUID id, String userName, ZonedDateTime creationDate) {
    this.id = id;
    this.userName = userName;
    this.creationDate = creationDate;
    this.blockedUsers = new ArrayList<>();
    this.posts = new ArrayList<>();
  }

  public UserContract(
      UUID id,
      String userName,
      ZonedDateTime creationDate,
      List<UUID> blockedUsers,
      List<UUID> posts) {
    this.id = id;
    this.userName = userName;
    this.creationDate = creationDate;
    this.blockedUsers = blockedUsers;
    this.posts = posts;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserContract that)) return false;
    return Objects.equals(id, that.id)
        && Objects.equals(userName, that.userName)
        && Objects.equals(creationDate, that.creationDate)
        && Objects.equals(blockedUsers, that.blockedUsers)
        && Objects.equals(posts, that.posts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userName, creationDate, blockedUsers, posts);
  }

  @Override
  public String toString() {
    return "UserContract{"
        + "id="
        + id
        + ", userName='"
        + userName
        + '\''
        + ", creationDate="
        + creationDate
        + ", blockedUsers="
        + blockedUsers
        + ", posts="
        + posts
        + '}';
  }
}
