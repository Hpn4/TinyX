package com.tinyx.controller;

import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.UserService;
import com.tinyx.user.contracts.UserContract;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Startup
@ApplicationScoped
public class RedisStreamUser extends RedisStreamReader<UserQuery> {

  @Inject UserService userService;

  public RedisStreamUser() {
    super();
  }

  @Inject
  public RedisStreamUser(final ReactiveRedisDataSource ds) {
    // The group is the service/repo name. It will be useful when there will be multiple k8s pods
    // for the
    // same service. For example if they are 3 repo-post running, messages will be balanced between
    // these 3 repo
    super(ds, UserQuery.class, "repo-post", RedisChannel.USER);
  }

  @Override
  public void process(List<UserQuery> data) {
    List<UserContract> createUsers = new ArrayList<>();
    List<UserContract> updateUsers = new ArrayList<>();
    for (UserQuery userQuery : data) {
      UserQuery.Operation operation = userQuery.operation;
      UserContract userContract = userQuery.user;

      switch (operation) {
        case CREATE:
          createUsers.add(userContract);
          break;
        case UPDATE:
          updateUsers.add(userContract);
          break;
        default:
          break;
      }
    }

    userService.createUser(createUsers);
    userService.updateUser(updateUsers);
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
