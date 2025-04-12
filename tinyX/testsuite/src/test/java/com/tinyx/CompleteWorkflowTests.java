package com.tinyx;

import static org.junit.jupiter.api.Assertions.*;

import com.tinyx.clients.*;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.requests.CreatePostRequest;
import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.contracts.UserContract;
import com.tinyx.user.converter.UserContractToLightUserContractConverter;
import groovy.lang.Tuple2;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompleteWorkflowTests {

  @Inject @RestClient HomeTimelineRestClient homeTimelineClient;
  @Inject @RestClient UserTimelineRestClient userTimelineClient;
  @Inject @RestClient UserRestClient userClient;
  @Inject @RestClient PostRestClient postClient;
  @Inject @RestClient ServiceMediaRestClient serviceMediaClient;
  @Inject @RestClient RepoMediaRestClient repoMediaClient;
  @Inject @RestClient SocialRestClient socialRestClient;
  @Inject @RestClient SearchRestClient searchClient;

  @Inject UserContractToLightUserContractConverter userToLight;

  @Inject TestsUtils testUtils;

  @Inject Logger logger;

  static Map<Integer, List<UserContract>> testingUsers =
      Collections.synchronizedMap(new HashMap<>());
  static List<Integer> testingUsersNumbers =
      Collections.synchronizedList(new ArrayList<>(List.of(5, 20, 100)));

  static Map<UUID, List<PostContract>> createdPosts = Collections.synchronizedMap(new HashMap<>());
  static List<UUID> createdMedias = Collections.synchronizedList(new ArrayList<>());

  @BeforeEach
  public void safetyDelay() throws InterruptedException {
    testUtils.sleep();
  }

  @ParameterizedTest
  @Order(1)
  @ValueSource(ints = {5, 20, 100})
  public void UserCreateAndCheck(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();
    testingUsers.put(n, Collections.synchronizedList(new ArrayList<>()));

    for (int i = 0; i < n; i++) {
      String name = testUtils.RandomUsername();

      stages.add(
          userClient
              .createUser(name)
              .thenCompose(
                  r1 -> {
                    return userClient
                        .getUserByName(name)
                        .thenCompose(
                            user -> {
                              assertEquals(name, user.userName);

                              return userClient
                                  .getUserById(user.id)
                                  .thenAccept(
                                      user2 -> {
                                        assertEquals(name, user2.userName);

                                        testingUsers
                                            .get(n)
                                            .add(user2); // Adding it to static hashmap so it can be
                                        // used by other tests
                                      });
                            });
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
  }

  @ParameterizedTest
  @Order(2)
  @ValueSource(ints = {5, 20, 100})
  public void HomeTimelineCheckTimelineCreated(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    for (UserContract user : testingUsers.get(n)) {
      stages.add(
          homeTimelineClient
              .getHomeTimeline(user.id)
              .thenAccept(
                  posts -> {
                    assertEquals(0, posts.size());
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
  }

  @ParameterizedTest
  @Order(3)
  @ValueSource(ints = {5, 20, 100})
  public void UserTimelineCheckTimelineCreated(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    for (UserContract user : testingUsers.get(n)) {
      stages.add(
          userTimelineClient
              .getUserTimeline(user.id)
              .thenAccept(
                  posts -> {
                    assertEquals(0, posts.size());
                  })
              .toCompletableFuture());
    }

    UUID authId = testingUsers.get(n).get(0).id;

    stages.add(
        userTimelineClient
            .getUsersTimeline(authId, testingUsers.get(n).stream().map(u -> u.id).toList())
            .thenAccept(
                posts -> {
                  assertEquals(0, posts.size());
                })
            .toCompletableFuture());

    testUtils.waitForFutures(stages);
  }

  @Test
  @Order(4)
  public void MediaCreateAndCheck() throws IOException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    List<Tuple2<InputStream, byte[]>> medias = testUtils.randomMedias(10);
    Map<UUID, byte[]> mediaBytes = Collections.synchronizedMap(new HashMap<>());
    Map<UUID, InputStream> receivedStreams = Collections.synchronizedMap(new HashMap<>());

    for (Tuple2<InputStream, byte[]> t : medias) {
      var c1 = repoMediaClient.uploadMediaEndpoint(t.getV1());
      stages.add(
          c1.thenAccept(
                  u -> {
                    createdMedias.add(u);
                    mediaBytes.put(u, t.getV2());
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
    testUtils.sleep();

    for (UUID mediaId : createdMedias) {
      var c2 = serviceMediaClient.doesMediaExistEndpoint(mediaId);

      stages.add(c2.thenAccept(Assertions::assertTrue).toCompletableFuture());
    }

    testUtils.waitForFutures(stages);

    for (UUID mediaId : createdMedias) {
      var c3 = serviceMediaClient.getMediaEndpoint(mediaId);

      stages.add(
          c3.thenAccept(
                  stream -> {
                    receivedStreams.put(mediaId, stream);
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);

    for (UUID mediaId : createdMedias) {
      byte[] bytes = testUtils.bytesFromStream(receivedStreams.get(mediaId));
      assertArrayEquals(mediaBytes.get(mediaId), bytes);
    }
  }

  @ParameterizedTest
  @Order(5)
  @ValueSource(ints = {5, 20, 100})
  public void PostsCreateAndCheck(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();
    Map<UUID, List<CreatePostRequest>> createdPostsReqs =
        Collections.synchronizedMap(new HashMap<>());

    for (UserContract user : testingUsers.get(n)) {
      List<CreatePostRequest> reqs = testUtils.randomPostCreationRequests(20);
      testUtils.assignRandomMediasToPostsRequests(reqs, createdMedias);

      for (CreatePostRequest req : reqs) {
        createdPostsReqs.put(user.id, Collections.synchronizedList(reqs));

        var c1 = postClient.newPostEndpoint(user.id, req);
        stages.add(c1.toCompletableFuture());
      }
    }

    testUtils.waitForFutures(stages);
    testUtils.longSleep();

    for (UserContract user : testingUsers.get(n)) {
      var c2 = postClient.queryUserPostsEndpoint(user.id, user.id);
      stages.add(
          c2.thenAccept(
                  userPosts -> {
                    assertEquals(createdPostsReqs.get(user.id).size(), userPosts.size());

                    for (PostContract post : userPosts) {
                      CreatePostRequest mReq =
                          createdPostsReqs.get(user.id).stream()
                              .filter(r -> r.content.equals(post.content))
                              .findFirst()
                              .get();

                      assertEquals(user.id, post.userId);
                      assertEquals(mReq.mediaId, post.mediaId);
                      assertEquals(mReq.parentId, post.parentId);
                      assertEquals(mReq.postType, post.postType);
                    }

                    createdPosts.put(user.id, Collections.synchronizedList(userPosts));
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);

    for (UserContract user : testingUsers.get(n)) {
      List<PostContract> userPosts = createdPosts.get(user.id);

      for (PostContract post : userPosts) {
        var c3 = postClient.queryPostEndpoint(user.id, post.id);

        stages.add(
            c3.thenAccept(
                    rpost -> {
                      assertEquals(post, rpost);
                    })
                .toCompletableFuture());
      }
    }

    testUtils.waitForFutures(stages);
  }

  @Order(6)
  @ParameterizedTest
  @ValueSource(ints = {5, 20, 100})
  public void UpdateUsersContracts(int n) {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    List<UserContract> currentContracts = testingUsers.get(n);
    List<UUID> usersIds = currentContracts.stream().map(u -> u.id).toList();

    var c1 = userClient.getUsersByIds(usersIds);

    stages.add(
        c1.thenAccept(
                contracts -> {
                  assertEquals(contracts.size(), currentContracts.size());
                  currentContracts.forEach(
                      cc -> assertTrue(contracts.stream().anyMatch(c -> cc.id.equals(c.id))));

                  for (UserContract c : contracts) {

                    List<PostContract> userPosts = createdPosts.get(c.id);
                    c.posts.forEach(
                        u -> assertTrue(userPosts.stream().anyMatch(p -> u.equals(p.id))));
                  }

                  testingUsers.put(n, contracts);
                })
            .toCompletableFuture());

    testUtils.waitForFutures(stages);
  }

  @ParameterizedTest
  @Order(7)
  @ValueSource(ints = {5, 20, 100})
  public void UserTimelineCheckCreatedPostsInTimeline(int n) {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    for (UserContract user : testingUsers.get(n)) {
      var c1 = userTimelineClient.getUserTimeline(user.id);

      stages.add(
          c1.thenAccept(
                  posts -> {
                    List<PostContract> userPosts = createdPosts.get(user.id);

                    assertEquals(userPosts.size(), posts.size());

                    for (PostContract post : userPosts)
                      assertTrue(posts.stream().anyMatch(u -> u.equals(post)));
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
  }

  @ParameterizedTest
  @Order(8)
  @ValueSource(ints = {5, 20, 100})
  public void SocialLikeAndCheck(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    HashMap<UUID, PostContract> chosenPost = new HashMap<>();

    for (UserContract user : testingUsers.get(n)) {
      List<UserContract> otherUsers =
          testingUsers.get(n).stream().filter(uc -> !uc.id.equals(user.id)).toList();

      PostContract post =
          testUtils.randomChoice(createdPosts.get(testUtils.randomChoice(otherUsers).id));
      chosenPost.put(user.id, post);

      var c1 = socialRestClient.postLikePostEndpoint(user.id, post.id);
      stages.add(c1.toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
    testUtils.longSleep();

    for (UserContract user : testingUsers.get(n)) {
      LightUserContract lightUser = userToLight.convert(user);
      PostContract post = chosenPost.get(user.id);

      var c2 = socialRestClient.getTargetLikersEndpoint(user.id, post.id);

      stages.add(
          c2.thenAccept(
                  likers -> {
                    LightUserContract matching =
                        likers.stream()
                            .filter(lu -> lu.id.equals(user.id))
                            .findFirst()
                            .orElseThrow();
                    assertEquals(lightUser, matching);
                  })
              .toCompletableFuture());

      var c3 = socialRestClient.getTargetLikedPostsEndpoint(user.id, user.id);

      stages.add(
          c3.thenAccept(
                  liked -> {
                    PostContract matching =
                        liked.stream()
                            .filter(po -> po.id.equals(post.id))
                            .findFirst()
                            .orElseThrow();
                    assertEquals(post, matching);
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
  }

  @ParameterizedTest
  @Order(9)
  @ValueSource(ints = {5, 20, 100})
  public void SocialFollowAndCheck(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    HashMap<UUID, LightUserContract> chosenFollow = new HashMap<>();

    for (UserContract user : testingUsers.get(n)) {
      List<UserContract> otherUsers =
          testingUsers.get(n).stream().filter(uc -> !uc.id.equals(user.id)).toList();
      LightUserContract follows = userToLight.convert(testUtils.randomChoice(otherUsers));
      chosenFollow.put(user.id, follows);

      var c1 = socialRestClient.postFollowTargetList(user.id, follows.id);
      stages.add(c1.toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
    testUtils.longSleep();

    for (UserContract user : testingUsers.get(n)) {
      LightUserContract lightUser = userToLight.convert(user);
      LightUserContract lightFollows = chosenFollow.get(user.id);

      var c2 = socialRestClient.getTargetFollowersEndpoint(lightFollows.id, lightFollows.id);

      stages.add(
          c2.thenAccept(
                  followers -> {
                    LightUserContract matching =
                        followers.stream()
                            .filter(fu -> fu.id.equals(user.id))
                            .findFirst()
                            .orElseThrow();
                    assertEquals(lightUser, matching);
                  })
              .toCompletableFuture());

      var c3 = socialRestClient.getTargetFollowsEndpoint(user.id, user.id);

      stages.add(
          c3.thenAccept(
                  followed -> {
                    LightUserContract matching =
                        followed.stream()
                            .filter(fu -> fu.id.equals(lightFollows.id))
                            .findFirst()
                            .orElseThrow();
                    assertEquals(lightFollows, matching);
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
  }

  @ParameterizedTest
  @Order(11)
  @ValueSource(ints = {5, 20, 100})
  public void SocialBlockAndCheck(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    HashMap<UUID, LightUserContract> chosenBlock = new HashMap<>();

    for (UserContract user : testingUsers.get(n)) {

      LightUserContract blocks =
          userToLight.convert(
              testUtils.randomChoice(
                  testingUsers.get(100).stream().filter(uc -> !uc.id.equals(user.id)).toList()));

      chosenBlock.put(user.id, blocks);

      var c1 = socialRestClient.postBlockTargetEndpoint(user.id, blocks.id);
      stages.add(c1.toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
    testUtils.longSleep();

    for (UserContract user : testingUsers.get(n)) {
      LightUserContract lightUser = userToLight.convert(user);
      LightUserContract lightBlocks = chosenBlock.get(user.id);

      var c2 = socialRestClient.getTargetBlockByEndpoint(lightBlocks.id, lightBlocks.id);

      stages.add(
          c2.thenAccept(
                  blockedBy -> {
                    assertTrue(blockedBy.stream().anyMatch(fo -> fo.equals(lightUser)));
                  })
              .toCompletableFuture());

      var c3 = socialRestClient.getTargetBlockListEndpoint(user.id, user.id);

      stages.add(
          c3.thenAccept(
                  blocked -> {
                    assertTrue(blocked.stream().anyMatch(fo -> fo.equals(lightBlocks)));
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
  }

  @ParameterizedTest
  @Order(10)
  @ValueSource(ints = {5, 20, 100})
  public void SearchAndCheck(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    for (UserContract user : testingUsers.get(n)) {
      List<PostContract> userPosts = createdPosts.get(user.id);
      StringBuilder phrase = new StringBuilder();

      for (PostContract post : userPosts) phrase.append(post.content.split(" ")[0]).append(" ");

      var c1 = searchClient.searchPost(user.id, phrase.toString(), null);

      stages.add(
          c1.thenAccept(
                  posts -> {
                    assertEquals(userPosts.size(), posts.size());

                    for (PostContract post : posts)
                      assertTrue(userPosts.stream().anyMatch(p -> p.equals(post)));
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
  }

  @ParameterizedTest
  @Order(12)
  @ValueSource(ints = {5, 20, 100})
  public void PostsDeleteAndCheck(int n) throws InterruptedException {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();

    for (UserContract user : testingUsers.get(n)) {
      List<PostContract> posts = createdPosts.get(user.id);

      for (PostContract post : posts) {
        var c1 = postClient.deletePostEndpoint(user.id, post.id);
        stages.add(c1.toCompletableFuture());
      }
    }

    testUtils.waitForFutures(stages);
    testUtils.longSleep();

    for (UserContract user : testingUsers.get(n)) {
      var c2 = postClient.queryUserPostsEndpoint(user.id, user.id);
      stages.add(
          c2.thenAccept(
                  userPosts -> {
                    assertEquals(0, userPosts.size());
                  })
              .toCompletableFuture());
    }

    testUtils.waitForFutures(stages);
  }
}
