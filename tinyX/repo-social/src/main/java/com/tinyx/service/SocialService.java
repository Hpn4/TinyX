package com.tinyx.service;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.RepoSocialRepository;
import com.tinyx.repository.entity.Post;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SocialService {

    @Inject
    RepoSocialRepository repoSocialRepository;

    public void createPost(List<PostContract> lpc)
    {
        for(var i = 0; i<lpc.size();i++){

            Post p = new Post(lpc.get(i).id);
            repoSocialRepository.CreatePost(p);
        }
    }

    public void deletePost(List<PostContract> lpc)
    {
        for(var i = 0; i< lpc.size();i++){
             repoSocialRepository.DeletePost(lpc.get(i).id);
        }
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
