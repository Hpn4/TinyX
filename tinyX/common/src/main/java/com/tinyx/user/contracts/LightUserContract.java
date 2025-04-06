package com.tinyx.user.contracts;

import java.time.ZonedDateTime;
import java.util.UUID;

public class LightUserContract {
  public UUID id;
  public String userName;
  public ZonedDateTime creationDate;

  public LightUserContract() {}

  public LightUserContract(UUID id, String userName, ZonedDateTime creationDate) {
    this.id = id;
    this.userName = userName;
    this.creationDate = creationDate;
  }
}
