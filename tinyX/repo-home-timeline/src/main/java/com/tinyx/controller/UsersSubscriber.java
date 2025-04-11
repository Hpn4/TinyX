package com.tinyx.controller;

import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.RepoHomeTimelineService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/** See {@link RedisStreamReader} for documentation. */
@Startup
@ApplicationScoped
public class UsersSubscriber extends RedisStreamReader<UserQuery> {

  @Inject RepoHomeTimelineService service;

  public UsersSubscriber() {
    super();
  }

  @Inject
  public UsersSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, UserQuery.class, "repo-home-timeline", RedisChannel.USER);
  }

  @Override
  public void process(List<UserQuery> data) {
    /**
     * Isolates the queries refering to a CREATE user operation, and feeds them to an initializing
     * service function so they can be added to the database.
     */
    var filtered =
        data.stream()
            .filter(q -> q.operation == UserQuery.Operation.CREATE)
            .map(u -> u.user.id)
            .toList();

    service.InitializeUsersHomeTimelines(filtered);
  }

  /* Mandatory stuff, timing might be put inside the application properties to be cleaner */
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
