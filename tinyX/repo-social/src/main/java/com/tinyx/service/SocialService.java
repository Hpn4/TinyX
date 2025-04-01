package com.tinyx.service;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.RepoSocialRepository;
import com.tinyx.repository.entity.Post;
import com.tinyx.repository.entity.User;
import com.tinyx.user.contracts.UserContract;
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

    public void createUser(List<UserContract> luc)
    {
        for(var i = 0;i<luc.size();i++)
        {
            User u = new User(luc.get(i).id);
            repoSocialRepository.CreateUser(u);
        }
    }

    public void deleteUser(List<UserContract> luc)
    {
        for(var i = 0; i< luc.size();i++)
        {
            repoSocialRepository.DeleteUser(luc.get(i).id);
        }
    }
    public void createRelation(UUID userId,UUID PostId, String relation,String t1, String t2)
    {

         repoSocialRepository.CreateRelation(userId, PostId,relation,t1,t2 );
    }

    public void deleteRelation(UUID userId, UUID postId, String relation, String t1, String t2)
    {
        repoSocialRepository.DeleteRelation(userId, postId,relation,t1,t2);
    }




}
