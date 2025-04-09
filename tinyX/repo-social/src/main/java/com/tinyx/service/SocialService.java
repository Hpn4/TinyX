package com.tinyx.service;

import com.tinyx.converter.PostQueryToPostEntityConverter;
import com.tinyx.redis.PostQuery;
import com.tinyx.repository.SocialRepository;
import com.tinyx.repository.UnfollowPublisher;
import com.tinyx.repository.UnlikePublisher;
import com.tinyx.repository.entity.PostEntity;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SocialService {

  @Inject SocialRepository repoSocialRepository;

  @Inject PostQueryToPostEntityConverter postQueryToPostEntityConverter;

  @Inject UnlikePublisher unlikePublisher;

  @Inject UnfollowPublisher unfollowPublisher;

  public void createPosts(List<PostQuery> queries) {
    repoSocialRepository.createPosts(postQueryToPostEntityConverter.convert(queries));
  }

  public void deletePosts(List<PostQuery> queries) {
    List<PostEntity> postsEntities = postQueryToPostEntityConverter.convert(queries);

    // Publish an unlike messages for each like relations linked to the post we want to delete
    for (PostEntity postEntity : postsEntities) {
      List<UUID> userIds = repoSocialRepository.getLikersId(postEntity.id);
      for (UUID userId : userIds) {
        unlikePublisher.publish(userId, postEntity.id);
      }
    }

    // Delete the post and all his likes relations
    repoSocialRepository.deletePosts(postsEntities);
  }

  public void createUsers(List<UUID> luc) {
    if (luc.isEmpty()) return;
    repoSocialRepository.createUsers(luc);
  }

  public void createRelations(
      List<SocialRelationEntity> lre, String relation, String t1, String t2) {
    if (lre.isEmpty()) return;

    if (relation != "BLOCK") {
      for (var i = 0; i < lre.size(); i++) {
        UUID blockId = null;
        if (relation == "LIKE") blockId = repoSocialRepository.getPostAuthor(lre.get(i).targetId);
        else if (relation == "FOLLOW") blockId = lre.get(i).targetId;
        if (repoSocialRepository.IsUserBlocked(lre.get(i).srcId, blockId)
            && repoSocialRepository.IsUserBlocked(blockId, lre.get(i).srcId)) {
          lre.remove(i);
          i--;
        }
      }
    } else {
      for (var i = 0; i < lre.size(); i++) {
        unfollowPublisher.publish(lre.get(i).srcId, lre.get(i).targetId);
        unfollowPublisher.publish(lre.get(i).targetId, lre.get(i).srcId);

        List<UUID> postIds =
            repoSocialRepository.getPostIdsFromUser(lre.get(i).srcId, lre.get(i).targetId);

        for (var j = 0; j < postIds.size(); j++) {
          unlikePublisher.publish(lre.get(i).srcId, postIds.get(i));
        }
      }
    }
    repoSocialRepository.createRelations(lre, relation, t1, t2);
  }

  public void deleteRelations(
      List<SocialRelationEntity> lre, String relation, String t1, String t2) {
    if (lre.isEmpty()) return;
    repoSocialRepository.deleteRelations(lre, relation, t1, t2);
  }
}
