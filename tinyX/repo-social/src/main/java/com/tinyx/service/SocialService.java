package com.tinyx.service;

import com.tinyx.repository.RepoSocialRepository;
import com.tinyx.repository.entity.Post;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class SocialService {

    @Inject
    RepoSocialRepository repoSocialRepository;

    public int createPost(UUID id)
    {
        Post p = new Post(id);
        return repoSocialRepository.CreatePost(p);
    }

    public int deletePost(UUID id)
    {
        return repoSocialRepository.DeletePost(id);
    }

    public int createLike(UUID userId, UUID PostId)
    {
        return repoSocialRepository.CreateLike(userId, PostId);
    }

    public  int deleteLike(UUID userId, UUID postId)
    {
        return repoSocialRepository.DeleteLike(userId, postId);
    }
}
