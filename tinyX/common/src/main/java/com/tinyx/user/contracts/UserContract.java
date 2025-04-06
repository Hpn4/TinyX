package com.tinyx.user.contracts;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserContract {
  public UUID id;
  public String userName;
  public ZonedDateTime creationDate;
  public List<UUID> blockedUsers;

  public UserContract() {}

  public UserContract(UUID id, String userName, ZonedDateTime creationDate) {
    this.id = id;
    this.userName = userName;
    this.creationDate = creationDate;
    this.blockedUsers = new ArrayList<>();
  }
}
