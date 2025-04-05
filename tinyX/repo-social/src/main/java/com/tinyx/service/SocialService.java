package com.tinyx.service;

import com.tinyx.repository.SocialRepository;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SocialService {

  @Inject
  SocialRepository repoSocialRepository;

  public void createPost(List<UUID> lpc) {
    if (lpc.isEmpty())
      return;
    repoSocialRepository.createPosts(lpc);
  }

  public void deletePost(List<UUID> lpc) {
    if(lpc.isEmpty())
      return;
    repoSocialRepository.deletePost(lpc);
  }

  public void createUsers(List<UUID> luc) {
    if(luc.isEmpty())
      return;
    repoSocialRepository.createUsers(luc);
  }

  public void deleteUser(List<UUID> luc) {
    if(luc.isEmpty())
      return;
    repoSocialRepository.deleteUser(luc);
  }

  public void createRelation(List<SocialRelationEntity> lre, String relation, String t1, String t2) {
    if(lre.isEmpty())
      return;
    repoSocialRepository.createRelation(lre,relation,t1,t2);
  }

  public void deleteRelation(List<SocialRelationEntity> lre, String relation, String t1, String t2) {
    if(lre.isEmpty())
      return;
    repoSocialRepository.deleteRelation(lre, relation, t1, t2);
  }
}
