package com.tinyx.timeline.contract;

import java.util.List;
import java.util.UUID;

public class UserTimelineContract {
  public List<UUID> posts;

  public UserTimelineContract() {}

  public UserTimelineContract(List<UUID> posts) {
    this.posts = posts;
  }
}
