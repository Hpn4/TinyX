package com.tinyx.service;

import com.tinyx.ErrorCodes;
import com.tinyx.repository.UserTimelineRepository;
import com.tinyx.timeline.contract.UserTimelineContract;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserTimelineService {

  @Inject UserTimelineRepository repository;

  public UserTimelineContract getUserTimeline(UUID userId) {
    repository.findByIdOptional(userId).orElseThrow(ErrorCodes.USER_NOT_FOUND.asSupplier(userId));

    return new UserTimelineContract(repository.findOrderedPostsForUser(userId));
  }

  public UserTimelineContract getUsersTimeline(List<UUID> usersId) {
    List<UUID> distinctIds = usersId.stream().distinct().toList();

    long count = repository.count("_id in ?1", distinctIds);
    if (count != distinctIds.size()) ErrorCodes.USERS_NOT_FOUND.throwError();

    return new UserTimelineContract(repository.findOrderedPostsByUsers(distinctIds));
  }
}
