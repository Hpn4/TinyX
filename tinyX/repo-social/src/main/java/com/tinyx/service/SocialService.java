package com.tinyx.service;

import com.tinyx.repository.SocialRepository;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SocialService {

  @Inject SocialRepository repoSocialRepository;

  public void createPosts(List<UUID> lpc) {
    if (lpc.isEmpty()) return;
    repoSocialRepository.createPosts(lpc);
  }

  public void deletePosts(List<UUID> lpc) {
    if (lpc.isEmpty()) return;
    repoSocialRepository.deletePosts(lpc);
  }

  public void createUsers(List<UUID> luc) {
    if (luc.isEmpty()) return;
    repoSocialRepository.createUsers(luc);
  }

  public void deleteUsers(List<UUID> luc) {
    if (luc.isEmpty()) return;
    repoSocialRepository.deleteUsers(luc);
  }

  public void createRelations(
      List<SocialRelationEntity> lre, String relation, String t1, String t2) {
    if (lre.isEmpty()) return;
    repoSocialRepository.createRelations(lre, relation, t1, t2);
  }

  public void deleteRelations(
      List<SocialRelationEntity> lre, String relation, String t1, String t2) {
    if (lre.isEmpty()) return;
    repoSocialRepository.deleteRelations(lre, relation, t1, t2);
  }
}
