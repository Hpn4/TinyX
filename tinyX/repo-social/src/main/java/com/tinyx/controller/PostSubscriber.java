package com.tinyx.controller;

import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.repository.entity.PostEntity;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Startup
@ApplicationScoped
public class PostSubscriber extends RedisStreamReader<PostQuery> {
  @Inject SocialService service;

  public PostSubscriber() {
    super();
  }

  @Inject
  public PostSubscriber(final ReactiveRedisDataSource ds) {
    super(ds, PostQuery.class, "repo-social", RedisChannel.POST);
  }

  @Override
  public void process(List<PostQuery> data) {

    /* List<UUID> creation =
            data.stream()
                .filter(q -> q.operation == PostQuery.Operation.CREATE)
                .map(q -> q.post.id)
                .toList();
        List<UUID> deletion =
            data.stream()
                .filter(q -> q.operation == PostQuery.Operation.DELETE)
                .map(q -> q.post.id)
                .toList();
    */
    List<PostEntity> creation = new ArrayList<>();
    List<PostEntity> deletion = new ArrayList<>();

    for (var i = 0; i < data.size(); i++) {
      PostEntity pe = new PostEntity(data.get(i).post.id, data.get(i).post.userId);
      if (data.get(i).operation == PostQuery.Operation.CREATE) creation.add(pe);
      else deletion.add(pe);
    }
    service.createPosts(creation);
    service.deletePosts(deletion);
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
