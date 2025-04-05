package com.tinyx.controller;

import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

@Startup
@ApplicationScoped
public class SocialStreamPost extends RedisStreamReader<PostQuery> {
  @Inject SocialService service;

  Logger log = Logger.getLogger(SocialRedisStreamUser.class);

  public SocialStreamPost() {
    super();
  }

  @Inject
  public SocialStreamPost(final ReactiveRedisDataSource ds) {
    super(ds, PostQuery.class, "repo-social", RedisChannel.POST);
  }

  @Override
  public void process(List<PostQuery> data) {

    List<UUID> creation =
        data.stream()
            .filter(q -> q.operation == PostQuery.Operation.CREATE)
            .map(q -> q.post.id)
            .toList();
    List<UUID> deletion =
        data.stream()
            .filter(q -> q.operation == PostQuery.Operation.DELETE)
            .map(q -> q.post.id)
            .toList();

    log.info("Created posts " + creation);
    log.info("Deleted posts " + deletion);
    service.createPost(creation);
    service.deletePost(deletion);
  }

  @Scheduled(every = "10m")
  @Override
  public void trimStream() {
    super.trimStream();
  }

  @Scheduled(every = "5s")
  @Override
  public void claimPendingMessages() {
    super.claimPendingMessages();
  }
}
