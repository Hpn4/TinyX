package com.tinyx.user.contracts;

import java.time.ZonedDateTime;
import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    LightUserContract that = (LightUserContract) o;
    return Objects.equals(id, that.id)
        && Objects.equals(userName, that.userName)
        && Objects.equals(creationDate, that.creationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userName, creationDate);
  }

  @Override
  public String toString() {
    return "LightUserContract{"
        + "id="
        + id
        + ", userName='"
        + userName
        + '\''
        + ", creationDate="
        + creationDate
        + '}';
  }
}
