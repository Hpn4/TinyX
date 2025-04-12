package com.tinyx;

import static org.junit.jupiter.api.Assertions.fail;

import com.tinyx.post.enumeration.PostType;
import com.tinyx.requests.CreatePostRequest;
import groovy.lang.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.opentest4j.AssertionFailedError;

@ApplicationScoped
public class TestsUtils {

  @Inject Logger logger;

  public String RandomUsername() {
    return "user-" + UUID.randomUUID().toString();
  }

  public String getRandomWord(int length) {
    String chars = "abcdefghijklmnopqrstuvwxyz";
    StringBuilder sb = new StringBuilder();
    Random rand = new Random();

    for (int i = 0; i < length; i++) sb.append(chars.charAt(rand.nextInt(chars.length())));

    return sb.toString();
  }

  public String RandomPostContent() {
    StringBuilder content = new StringBuilder();

    for (int i = 0; i < 10; i++) content.append(getRandomWord(10)).append(" ");

    return content.toString();
  }

  int SLIGHT_DELAY = 100;
  int DELAY = 500;
  int HIGH_DELAY = 1000;

  public void msSleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ignored) {
      fail(ignored);
    }
  }

  public void shortSleep() {
    msSleep(SLIGHT_DELAY);
  }

  public void sleep() {
    msSleep(DELAY);
  }

  public void longSleep() {
    msSleep(HIGH_DELAY);
  }

  public void waitForFutures(List<CompletableFuture<?>> futures) {
    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
    } catch (Exception e) {
      logger.error("Testsuite encountered an an exception:");
      e.printStackTrace();
      fail();
    }
  }

  public CreatePostRequest randomPostCreationRequest(UUID parentId, UUID mediaId) {
    return new CreatePostRequest(RandomPostContent(), parentId, mediaId, PostType.NONE);
  }

  public List<CreatePostRequest> randomPostCreationRequests(int n) {
    List<CreatePostRequest> requests = new ArrayList<>();

    for (int i = 0; i < n; i++) {
      requests.add(randomPostCreationRequest(null, null));
    }
    return requests;
  }

  public void assignRandomMediasToPostsRequests(
      List<CreatePostRequest> requests, List<UUID> medias) {
    Random random = new Random();

    for (CreatePostRequest request : requests) {
      if (random.nextBoolean()) {
        request.mediaId = medias.get(random.nextInt(medias.size()));
      }
    }
  }

  public byte[] bytesFromStream(InputStream s) {
    try {
      return s.readAllBytes();
    } catch (IOException e) {
      throw new AssertionFailedError();
    }
  }

  public List<Tuple2<InputStream, byte[]>> randomMedias(int n) throws IOException {
    List<Tuple2<InputStream, byte[]>> medias = new ArrayList<>();

    for (int i = 0; i < n; i++) {
      URL url = new URL("https://picsum.photos/500");
      InputStream ms = url.openStream();

      byte[] byteArray = ms.readAllBytes();

      InputStream fs = new ByteArrayInputStream(byteArray);
      medias.add(new Tuple2<>(fs, byteArray));

      ms.close();
    }

    return medias;
  }

  public <T> T randomChoice(List<T> choices) {
    Random random = new Random();
    return choices.get(random.nextInt(choices.size()));
  }

  public void getException(CompletableFuture<?> future, int code) {
    try {
      future.join();
      assert false : "Expected conflict exception (" + code + ") but request succeeded";
    } catch (CompletionException e) {
      if (e.getCause() instanceof ClientWebApplicationException) {
        ClientWebApplicationException ce = (ClientWebApplicationException) e.getCause();
        assert ce.getResponse().getStatus() == code
            : "Expected " + code + ", got " + ce.getResponse().getStatus();
      } else {
        throw e;
      }
    }
  }
}
