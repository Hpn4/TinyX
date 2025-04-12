package com.tinyx;

import com.tinyx.clients.*;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.enumeration.PostType;
import com.tinyx.requests.CreatePostRequest;
import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.contracts.UserContract;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ErrorTest {
  @Inject @RestClient HomeTimelineRestClient homeTimelineClient;
  @Inject @RestClient UserTimelineRestClient userTimelineClient;
  @Inject @RestClient UserRestClient userClient;
  @Inject @RestClient PostRestClient postClient;
  @Inject @RestClient ServiceMediaRestClient serviceMediaClient;
  @Inject @RestClient RepoMediaRestClient repoMediaClient;
  @Inject @RestClient SocialRestClient socialRestClient;
  @Inject @RestClient SearchRestClient searchClient;

  @Inject TestsUtils testUtils;

  @Inject Logger logger;

  static List<UserContract> testingUsers = Collections.synchronizedList(new ArrayList<>());

  static List<PostContract> createdPosts = Collections.synchronizedList(new ArrayList<>());
  static List<UUID> createdMedias = Collections.synchronizedList(new ArrayList<>());
  static LightUserContract user1;
  static LightUserContract user2;

  @BeforeEach
  public void safetyDelay() throws InterruptedException {
    testUtils.sleep();
  }

  @Test
  @Order(1)
  public void errorUser() {
    String name = testUtils.RandomUsername();

    testUtils.getException(userClient.getUserByName(name).toCompletableFuture(), 404);

    testUtils.getException(userClient.getUserById(UUID.randomUUID()).toCompletableFuture(), 404);

    userClient
        .createUser(name)
        .thenAccept(
            r1 -> {
              assert (r1.getStatus() == 200);
            })
        .toCompletableFuture()
        .join();

    user1 = userClient.getUserByName(name).toCompletableFuture().join();

    testUtils.getException(userClient.createUser(name).toCompletableFuture(), 409);

    List<UUID> uuids = new ArrayList<>();
    uuids.add(UUID.randomUUID());
    testUtils.getException(userClient.getUsersByIds(uuids).toCompletableFuture(), 404);
  }

  @Test
  @Order(2)
  public void errorPost1() {
    String name = testUtils.RandomUsername();
    userClient
        .createUser(name)
        .thenAccept(
            r1 -> {
              assert (r1.getStatus() == 200);
            })
        .toCompletableFuture()
        .join();
    testUtils.sleep();

    user2 = userClient.getUserByName(name).toCompletableFuture().join();

    testUtils.getException(
        postClient.deletePostEndpoint(user1.id, UUID.randomUUID()).toCompletableFuture(), 404);

    CreatePostRequest postRequest =
        new CreatePostRequest("some content", null, null, PostType.NONE);
    postClient
        .newPostEndpoint(user1.id, postRequest)
        .thenAccept(
            r1 -> {
              assert (r1.getStatus() == 200);
            })
        .toCompletableFuture()
        .join();
    testUtils.sleep();
    List<PostContract> posts =
        postClient.queryUserPostsEndpoint(user1.id, user1.id).toCompletableFuture().join();
    assert posts.size() == 1 : "Expected 1 post but got " + posts.size();
    PostContract post = posts.get(0);

    testUtils.getException(
        postClient.deletePostEndpoint(user2.id, post.id).toCompletableFuture(), 403);

    postClient.deletePostEndpoint(user1.id, post.id).toCompletableFuture().join();
  }

  @Test
  @Order(3)
  public void errorBlock() {
    testUtils.getException(
        socialRestClient.postBlockTargetEndpoint(UUID.randomUUID(), user1.id).toCompletableFuture(),
        404);

    testUtils.getException(
        socialRestClient.postBlockTargetEndpoint(user1.id, UUID.randomUUID()).toCompletableFuture(),
        404);

    socialRestClient.postBlockTargetEndpoint(user1.id, user2.id).toCompletableFuture().join();
    testUtils.sleep();

    testUtils.getException(
        socialRestClient.postBlockTargetEndpoint(user1.id, user2.id).toCompletableFuture(), 409);

    socialRestClient.deleteBlockTargetEndpoint(user1.id, user2.id).toCompletableFuture().join();
    testUtils.sleep();
  }

  @Test
  @Order(4)
  public void errorSearch1() {
    testUtils.getException(
        searchClient.searchPost(user1.id, null, null).toCompletableFuture(), 400);
  }

  @Test
  @Order(5)
  public void errorUserTimeline() {
    testUtils.getException(
        userTimelineClient.getUserTimeline(UUID.randomUUID()).toCompletableFuture(), 404);
    List<UUID> uuids = new ArrayList<>();
    uuids.add(user1.id);
    uuids.add(UUID.randomUUID());
    testUtils.getException(
        userTimelineClient.getUsersTimeline(user1.id, uuids).toCompletableFuture(), 404);
  }

  @Test
  @Order(6)
  public void errorHomeTimeline() {
    testUtils.getException(
        homeTimelineClient.getHomeTimeline(UUID.randomUUID()).toCompletableFuture(), 404);
  }
}
