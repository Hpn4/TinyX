package com.tinyx.service;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.RepoSocialRepository;
import com.tinyx.repository.entity.Post;
import com.tinyx.repository.entity.User;
import com.tinyx.user.contracts.UserContract;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SocialService {

    @Inject
    RepoSocialRepository repoSocialRepository;

    public void createPost(List<PostContract> lpc)
    {
        if(lpc.size()==0)
            return;
        String posts = "";//"MERGE (p:Post {id: +post.Id.toString()+})";
        for(var i = 0; i<lpc.size();i++){

            posts += "(:Post {id: +"+lpc.get(i).id+"+})";
            if(i!=lpc.size()-1)
                posts += ", ";
        }
        repoSocialRepository.CreatePost(posts);
    }

    public void deletePost(List<PostContract> lpc)
    {
        if(lpc.size()==0)
            return;
        String posts = "[";
        for(var i = 0; i< lpc.size();i++){
            posts +=lpc.get(i).id;
            if(i!=lpc.size()-1)
                posts += ", ";
        }
        posts += "]";
        repoSocialRepository.DeletePost(posts);
    }

    public void createUser(List<UserContract> luc)
    {
        if(luc.size()==0)
            return;
        String users = "";
        for(var i = 0;i<luc.size();i++)
        {
            users += "(:User {id: +"+luc.get(i).id+"+})";
            if(i!=luc.size()-1)
                users += ", ";

        }
        repoSocialRepository.CreateUser(users);
    }

    public void deleteUser(List<UserContract> luc)
    {
        if(luc.size()==0)
            return;
        String users = "[";
        for(var i = 0; i< luc.size();i++)
        {
            users +=luc.get(i).id;
            if(i!=luc.size()-1)
                users += ", ";
        }
        users+="]";
        repoSocialRepository.DeleteUser(users);
    }

    public void createRelation(List<List<UUID>> Id, String relation,String t1, String t2)
    {
        if(Id.size()==0)
            return;
        String likes = "";
        for(var i =0; i< Id.size();i++)
        {
            likes += "(u:"+t1+" {id:"+Id.get(i).get(0)+"})-[:"+relation+" {creationTime:'"+ Instant.now().toString()+"'}]->(p:"+t2+" {id:"+Id.get(i).get(1)+"})";
            if(i!=Id.size()-1)
                likes +=", ";
        }
         repoSocialRepository.CreateRelation(likes);
    }

    public void deleteRelation(List<List<UUID>> ids, String relation, String t1, String t2)
    {
        if(ids.size()==0)
            return;
        String ids1 = "[";
        String ids2 = "[";
        for(var i = 0; i< ids.size(); i++)
        {
            ids1 += ids.get(i).get(0);
            ids2 += ids.get(i).get(1);
            if(i!=ids.size()-1){
                ids1+=", ";
                ids2+=", ";
            }
        }
        ids1 +="]";
        ids2 += "]";
        //"MATCH (u:"+type1+" {id:"+UserId.toString()+"})-[r:"+relation+"]->(p:"+type2+" {id:"+PostId+"}) DELETE r"
        repoSocialRepository.DeleteRelation(ids1, ids2,relation,t1,t2);
    }




}
