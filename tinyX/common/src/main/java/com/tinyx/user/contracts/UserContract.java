package com.tinyx.user.contracts;

import java.time.LocalDate;
import java.util.UUID;

public class UserContract {
  public UUID id;
  public String userName;
  public LocalDate creationDate;

  public UserContract(UUID id, String userName, LocalDate creationDate) {

    this.id = id;
    this.userName = userName;
    this.creationDate = creationDate;
  }
}
