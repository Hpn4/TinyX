package com.tinyx.controller;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.repository.entity.Post;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Startup
@ApplicationScoped
public class RedisStreamLikePostSocial extends RedisStreamReader<LikePostQuery> {
    @Inject
    SocialService service;
    public RedisStreamLikePostSocial(){super();}

    public RedisStreamLikePostSocial(final ReactiveRedisDataSource ds)
    {
        super(ds, LikePostQuery.class,"repo-social", RedisChannel.POST);
    }

    @Override
    public void process(List<LikePostQuery> data)
    {
        List<List<UUID>> likes = new ArrayList<>();
        List<List<UUID>> dislikes = new ArrayList<>();

        for(var i = 0; i<data.size();i++)
        {
            if(data.get(i).operation == LikePostQuery.Operation.LIKE)
            {
                List<UUID> likeIds = new ArrayList<>(2);
                likeIds.add(data.get(i).srcUserId);
                likeIds.add(data.get(i).targetPostId);
                likes.add(likeIds);
            }
            else
            {
                List<UUID> likeIds = new ArrayList<>(2);
                likeIds.add(data.get(i).srcUserId);
                likeIds.add(data.get(i).targetPostId);
                dislikes.add(likeIds);
            }

        }
        service.createRelation(likes,"LIKE","User","Post");
        service.deleteRelation(dislikes,"LIKE","User", "Post");


    }



    @Scheduled(every = "10m")
    @Override
    public void trimStream()
    {
        super.trimStream();
    }

    @Scheduled(every = "5s")
    @Override
    public void claimPendingMessages(){
        super.claimPendingMessages();
    }

}
