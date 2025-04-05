package com.tinyx.controller;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.repository.entity.SocialRelationEntity;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.jboss.logging.Logger;

@Startup
@ApplicationScoped
public class SocialRedisStreamLikePost extends RedisStreamReader<LikePostQuery> {
  @Inject SocialService service;

  Logger log = Logger.getLogger(SocialRedisStreamUser.class);

  public SocialRedisStreamLikePost() {
    super();
  }

  @Inject
  public SocialRedisStreamLikePost(final ReactiveRedisDataSource ds) {
    super(ds, LikePostQuery.class, "repo-social", RedisChannel.SOCIAL);
  }

  @Override
  public void process(List<LikePostQuery> data) {
    List<SocialRelationEntity> likes = new ArrayList<>();
    List<SocialRelationEntity> dislikes = new ArrayList<>();

    for (var i = 0; i < data.size(); i++) {
      if (data.get(i).operation == LikePostQuery.Operation.LIKE) {
        likes.add(new SocialRelationEntity(data.get(i).srcUserId, data.get(i).targetPostId));
      } else {
        dislikes.add(new SocialRelationEntity(data.get(i).srcUserId, data.get(i).targetPostId));
      }
    }
    log.info("create LIKE");
    service.createRelation(likes, "LIKE", "User", "Post");
    service.deleteRelation(dislikes, "LIKE", "User", "Post");
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
