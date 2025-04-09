package com.tinyx.timeline.utils;

import com.tinyx.timeline.entity.UserTimelineEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class TimelineTestUtils {

  public ZonedDateTime randomZonedDateTime() {
    long startEpoch =
        ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant().toEpochMilli();
    long endEpoch = Instant.now().toEpochMilli();
    long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
    return Instant.ofEpochMilli(randomEpoch).atZone(ZoneId.systemDefault());
  }

  public UserTimelineEntity addAndFillUser(int postsCount) {
    UserTimelineEntity userTimelineEntity = new UserTimelineEntity();
    userTimelineEntity.id = UUID.randomUUID();
    userTimelineEntity.posts = new ArrayList<>();

    for (int i = 0; i < postsCount; i++) {
      UserTimelineEntity.UserTimelinePostEntry userTimelinePostEntry =
          new UserTimelineEntity.UserTimelinePostEntry();
      userTimelinePostEntry.id = UUID.randomUUID();
      userTimelinePostEntry.timestamp = randomZonedDateTime();

      userTimelineEntity.posts.add(userTimelinePostEntry);
    }

    return userTimelineEntity;
  }

  public List<UUID> sortTimeline(List<UserTimelineEntity> users) {
    return users.stream()
        .flatMap(e -> e.posts.stream())
        .sorted(
            Comparator.comparing((UserTimelineEntity.UserTimelinePostEntry e) -> e.timestamp)
                .reversed())
        .map(e -> e.id)
        .toList();
  }
}
