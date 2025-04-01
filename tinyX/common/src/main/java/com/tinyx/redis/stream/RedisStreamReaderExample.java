package com.tinyx.redis.stream;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.PostQuery;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@Startup
@ApplicationScoped
public class RedisStreamReaderExample extends RedisStreamReader<PostQuery> {

  public RedisStreamReaderExample() {
    super();
  }

  @Inject
  public RedisStreamReaderExample(final ReactiveRedisDataSource ds) {
    // The group is the service/repo name. It will be useful when there will be multiple k8s pods
    // for the
    // same service. For example if they are 3 repo-post running, messages will be balanced between
    // these 3 repo
    super(ds, PostQuery.class, "svc-post", RedisChannel.POST);
  }

  @Override
  public void process(List<PostQuery> data) {
    // Do stuff with the list of queries
  }

  /* Simple example on how to publish */
  @Inject RedisPublisherFactory redisPublisherFactory;

  protected void post() {
    PostQuery postQuery = new PostQuery();
    postQuery.post = new PostContract();
    postQuery.post.content = "svc-post";
    redisPublisherFactory
        .<PostQuery>createPublisher()
        .publishStream(RedisChannel.POST, postQuery, PostQuery.class);
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
