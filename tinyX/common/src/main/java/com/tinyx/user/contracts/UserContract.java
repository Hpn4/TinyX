package com.tinyx.user.contracts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserContract {
  public UUID id;
  public String userName;
  public LocalDate creationDate;
  public List<UUID> blockedUsers;

  public UserContract(UUID id, String userName, LocalDate creationDate) {

    this.id = id;
    this.userName = userName;
    this.creationDate = creationDate;
    this.blockedUsers = new ArrayList<>();
  }
}
