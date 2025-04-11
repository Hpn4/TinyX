package com.tinyx.controller;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.MediaService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

@ApplicationScoped
@Startup
public class RedisMediaSubscriber extends RedisStreamReader<PostQuery> {
  @Inject MediaService mediaService;

  @Inject Logger logger;

  public RedisMediaSubscriber() {
    super();
  }

  @Inject
  public RedisMediaSubscriber(final ReactiveRedisDataSource ds) {
    // The group is the service/repo name. It will be useful when there will be multiple k8s pods
    // for the
    // same service. For example if they are 3 repo-post running, messages will be balanced between
    // these 3 repo
    super(ds, PostQuery.class, "repo-media", RedisChannel.POST);
  }

  /**
   * Catches Post-related messages to keep the database updated regarding the post backreference
   * that is stored with medias. This is also used for media deletion.
   *
   * <p>The update operation is not handled here as it is not handled in the post service as well.
   *
   * @param data The post queries to handle.
   */
  @Override
  public void process(List<PostQuery> data) {
    Map<PostQuery.Operation, List<PostContract>> oMap =
        Map.of(
            PostQuery.Operation.CREATE,
            new ArrayList<>(),
            PostQuery.Operation.DELETE,
            new ArrayList<>());

    // Filtering out Update queries (that shouldn't happen anyways)
    data.stream()
        .filter(q -> q.operation != PostQuery.Operation.UPDATE)
        .forEach(q -> oMap.get(q.operation).add(q.post));

    mediaService.handleCreatePost(oMap.get(PostQuery.Operation.CREATE));
    mediaService.handleDeletePost(oMap.get(PostQuery.Operation.DELETE));
  }

  @Scheduled(every = "{tinyx.redis-stream.trim.every}")
  @Override
  protected void trimStream() {
    super.trimStream();
  }

  @Scheduled(every = "{tinyx.redis-stream.claim.every}")
  @Override
  protected void claimPendingMessages() {
    super.claimPendingMessages();
  }
}
