package com.tinyx.user;

import com.tinyx.redis.UserQuery;
import com.tinyx.redis.UserRelationsQuery;
import com.tinyx.user.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@ApplicationScoped
public class UserTestUtils {

  @Inject UserConverter userConverter;

  public String RandomUsername() {
    return "user" + UUID.randomUUID().toString().substring(0, 8);
  }

  public List<UserEntity> randomUsers(int n) {
    ArrayList<UserEntity> users = new ArrayList<>();

    for (int i = 0; i < n; i++)
      users.add(new UserEntity(UUID.randomUUID(), RandomUsername(), LocalDate.now()));

    return users;
  }

  public List<UserQuery> randomUserCreationQueries(int n) {
    return randomUsers(n).stream()
        .map(u -> new UserQuery(UserQuery.Operation.CREATE, userConverter.convertUser(u)))
        .toList();
  }

  public List<UserRelationsQuery> randomRelationsQueriesBetweenUsers(
      List<UserQuery> userQueries, UserRelationsQuery.Operation operation, int n) {
    if (userQueries.size() == 1)
      throw new IllegalArgumentException(
          "List of user queries cannot be of size 1 here, that would run forever.");

    ArrayList<UserRelationsQuery> relationsQueries = new ArrayList<>();

    Random r = new Random();

    int safeGuard = 0;

    while (relationsQueries.size() < n) {
      if (safeGuard == userQueries.size() * 10)
        throw new IllegalArgumentException(
            "Test seems to be stuck, make sure the list of users you give is correct.");

      UserQuery first = userQueries.get(r.nextInt(userQueries.size()));
      UserQuery second = userQueries.get(r.nextInt(userQueries.size()));

      if (first.user.id == second.user.id) continue;

      if (relationsQueries.stream()
          .anyMatch(rq -> rq.srcUserId == first.user.id && rq.targetUserId == second.user.id))
        continue;

      relationsQueries.add(
          new UserRelationsQuery(operation, first.user.id, second.user.id, ZonedDateTime.now()));

      safeGuard++;
    }

    return relationsQueries;
  }
}
