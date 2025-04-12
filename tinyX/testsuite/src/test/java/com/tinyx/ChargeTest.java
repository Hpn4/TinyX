package com.tinyx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tinyx.clients.*;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.enumeration.PostType;
import com.tinyx.requests.CreatePostRequest;
import com.tinyx.user.contracts.UserContract;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
public class ChargeTest {

  @Inject @RestClient HomeTimelineRestClient homeTimelineClient;
  @Inject @RestClient UserTimelineRestClient userTimelineClient;
  @Inject @RestClient UserRestClient userClient;
  @Inject @RestClient PostRestClient postClient;
  @Inject @RestClient ServiceMediaRestClient serviceMediaClient;
  @Inject @RestClient RepoMediaRestClient repoMediaClient;

  @Inject TestsUtils testUtils;

  @Inject Logger logger;

  static Map<Integer, List<UserContract>> testingUsers =
      Collections.synchronizedMap(new HashMap<>());
  static List<Integer> testingUsersNumbers = Collections.synchronizedList(new ArrayList<>());

  static Map<UUID, List<PostContract>> createdPosts = Collections.synchronizedMap(new HashMap<>());
  static List<UUID> createdMedias = Collections.synchronizedList(new ArrayList<>());

  @ParameterizedTest
  @ValueSource(ints = {10, 100})
  public void testCharge(int numberOfUsers) {
    ArrayList<CompletableFuture<?>> stages = new ArrayList<>();
    testingUsers.put(numberOfUsers, Collections.synchronizedList(new ArrayList<>()));

    for (int i = 0; i < numberOfUsers; i++) {
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
                                            .get(numberOfUsers)
                                            .add(user2); // Adding it to static hashmap so it can be
                                        // used by other tests
                                      });
                            });
                  })
              .toCompletableFuture());
    }
    stages = new ArrayList<>();
    for (var user : testingUsers.get(numberOfUsers)) {
      for (int i = 0; i < numberOfUsers; i++) {
        stages.add(
            postClient
                .newPostEndpoint(
                    user.id,
                    new CreatePostRequest(testUtils.RandomPostContent(), null, null, PostType.NONE))
                .toCompletableFuture());
      }
    }
    testUtils.waitForFutures(stages);
    stages = new ArrayList<>();
    List<PostContract> postContracts = new ArrayList<>();
    for (var user : testingUsers.get(numberOfUsers)) {
      stages.add(
          postClient
              .queryPostEndpoint(user.id, user.id)
              .thenCompose(
                  post -> {
                    postContracts.add(post);
                    return null;
                  })
              .toCompletableFuture());
    }
    testUtils.waitForFutures(stages);
    /*stages = new ArrayList<>();
    for (var post : postContracts) {
      //stages.add(postClient.deletePostEndpoint(post.userId, post.id).toCompletableFuture());
    }*/

    // TODO Add a lot of like
    // TODO add a lot of blocked user
    // TODO add replies and repost
    // TODO add media
  }
}
