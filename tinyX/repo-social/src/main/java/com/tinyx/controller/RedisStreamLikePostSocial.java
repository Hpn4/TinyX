package com.tinyx.controller;

import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

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
        for(var i = 0; i<data.size();i++)
        {
            if(data.get(i).operation== LikePostQuery.Operation.LIKE)
                service.createLike(data.get(i).srcUserId,data.get(i).targetPostId);
            else
                service.deleteLike(data.get(i).srcUserId,data.get(i).targetPostId);
        }

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
