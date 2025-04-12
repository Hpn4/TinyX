package com.tinyx;

import static io.restassured.RestAssured.given;

import com.tinyx.repository.UserTimelineRepository;
import com.tinyx.timeline.entity.UserTimelineEntity;
import com.tinyx.timeline.utils.TimelineTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UsersTimelineTest {

  @Inject UserTimelineRepository userTimelineRepository;

  @Inject TimelineTestUtils timelineTestUtils;

  @Test
  public void notFoundUser() {
    given()
        .contentType("application/json")
        .when()
        .header("X-User", UUID.randomUUID())
        .body(List.of(UUID.randomUUID()))
        .get("/timeline/users")
        .then()
        .statusCode(404);
  }

  @Test
  public void badFormattedUser() {
    given()
        .contentType("application/json")
        .when()
        .header("X-User", "Hey_a_string")
        .body(List.of(UUID.randomUUID()))
        .get("/timeline/users")
        .then()
        .statusCode(400);
  }

  @Test
  public void badFormattedUserList() {
    given()
        .contentType("application/json")
        .when()
        .header("X-User", UUID.randomUUID())
        .body("")
        .get("/timeline/users")
        .then()
        .statusCode(400);
  }

  @Test
  public void badFormattedUserList_1() {
    given()
        .contentType("application/json")
        .when()
        .header("X-User", UUID.randomUUID())
        .body(List.of())
        .get("/timeline/users")
        .then()
        .statusCode(400);
  }

  @Test
  public void notFoundUserInList() {
    UserTimelineEntity userTimelineEntity = timelineTestUtils.addAndFillUser(1);

    userTimelineRepository.persist(userTimelineEntity);

    given()
        .contentType("application/json")
        .when()
        .header("X-User", userTimelineEntity.id)
        .body(List.of(UUID.randomUUID()))
        .get("/timeline/users")
        .then()
        .statusCode(404);

    userTimelineRepository.deleteAll();
  }

  @Test
  public void singleUserTimeline() {
    // A user with 1000 posts with random dates
    UserTimelineEntity user = timelineTestUtils.addAndFillUser(1000);
    userTimelineRepository.persist(user);

    List<UUID> postsId = timelineTestUtils.sortTimeline(List.of(user));

    Assertions.assertEquals(
        postsId, userTimelineRepository.findOrderedPostsByUsers(List.of(user.id)));

    userTimelineRepository.deleteAll();
  }

  @Test
  public void multipleUsersTimeline() {
    // Generate multiple users
    List<UserTimelineEntity> users = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      UserTimelineEntity userTimelineEntity = timelineTestUtils.addAndFillUser(i * 10);

      userTimelineRepository.persist(userTimelineEntity);
      users.add(userTimelineEntity);
    }

    // Extract all user ids
    List<UUID> usersId = users.stream().map(u -> u.id).toList();

    // Build the expected output
    List<UUID> postsId = timelineTestUtils.sortTimeline(users);

    Assertions.assertEquals(postsId, userTimelineRepository.findOrderedPostsByUsers(usersId));

    userTimelineRepository.deleteAll();
  }
}
