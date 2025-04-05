package com.tinyx.service;

import com.tinyx.home.entity.HomeTimelineMongoEntity;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.SvcHomeTimelineRepository;
import com.tinyx.repository.UserTimelineRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class SvcHomeTimelineService {
  @Inject SvcHomeTimelineRepository repository;

  @Inject @RestClient UserTimelineRestClient userTimelineClient;

  /**
   * Retrieves a given user's home timeline (aka list of posts).
   *
   * @param userId The target user.
   * @throws org.jboss.resteasy.reactive.ClientWebApplicationException on unreachable UserTimeline
   *     service.
   * @return The list of posts shaping the user's timeline.
   */
  public List<PostContract> GetUserTimeline(UUID userId) {
    HomeTimelineMongoEntity mongoUser = repository.findByIdOptional(userId).orElseThrow();

    return userTimelineClient.GetUsersTimeline(mongoUser.timelineIds);
  }
}
