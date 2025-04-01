package com.tinyx.controller;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.PostService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Startup
@ApplicationScoped
public class RedisStreamPost extends RedisStreamReader<PostQuery> {

  @Inject PostService postService;

  public RedisStreamPost() {
    super();
  }

  @Inject
  public RedisStreamPost(final ReactiveRedisDataSource ds) {
    // The group is the service/repo name. It will be useful when there will be multiple k8s pods
    // for the
    // same service. For example if they are 3 repo-post running, messages will be balanced between
    // these 3 repo
    super(ds, PostQuery.class, "repo-post", RedisChannel.POST);
  }

  @Override
  public void process(List<PostQuery> data) {
    List<PostContract> createPosts = new ArrayList<>();
    List<UUID> deletePosts = new ArrayList<>();
    List<PostContract> updatePosts = new ArrayList<>();
    for (PostQuery postQuery : data) {
      PostQuery.Operation operation = postQuery.operation;
      PostContract postContract = postQuery.post;

      switch (operation) {
        case CREATE:
          createPosts.add(postContract);
          break;
        case DELETE:
          deletePosts.add(postContract.id);
          break;
        case UPDATE:
          updatePosts.add(postContract);
          break;
        default:
          break;
      }
    }

    postService.createPost(createPosts);
    postService.deletePost(deletePosts);
    postService.updatePost(updatePosts);
  }

  /* Mandatory stuff, timing might be put inside the application properties to be cleaner */
  @Scheduled(every = "10m")
  @Override
  protected void trimStream() {
    super.trimStream();
  }

  @Scheduled(every = "5s")
  @Override
  protected void claimPendingMessages() {
    super.claimPendingMessages();
  }
}
