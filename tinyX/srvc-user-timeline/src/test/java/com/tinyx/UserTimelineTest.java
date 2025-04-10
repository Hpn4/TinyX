package com.tinyx;

import static io.restassured.RestAssured.given;

import com.tinyx.repository.UserTimelineRepository;
import com.tinyx.timeline.entity.UserTimelineEntity;
import com.tinyx.timeline.utils.TimelineTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserTimelineTest {

  @Inject UserTimelineRepository userTimelineRepository;

  @Inject TimelineTestUtils timelineTestUtils;

  @Test
  public void notFoundUser() {
    given()
        .contentType("application/json")
        .when()
        .header("X-User", UUID.randomUUID())
        .get("/timeline/user")
        .then()
        .statusCode(404);
  }

  @Test
  public void badFormattedUser() {
    given()
        .contentType("application/json")
        .when()
        .header("X-User", "Hey_a_string")
        .get("/timeline/user")
        .then()
        .statusCode(400);
  }

  @Test
  public void singleUserTimeline() {
    // A user with 1000 posts with random dates
    UserTimelineEntity user = timelineTestUtils.addAndFillUser(1000);
    timelineTestUtils.sortTimeline(List.of(user));

    userTimelineRepository.persist(user);

    List<UUID> postsId = timelineTestUtils.sortTimeline(List.of(user));

    Assertions.assertEquals(postsId, userTimelineRepository.findOrderedPostsForUser(user.id));

    userTimelineRepository.delete(user);
  }
}
