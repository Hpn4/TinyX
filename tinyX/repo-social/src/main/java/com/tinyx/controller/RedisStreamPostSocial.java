package com.tinyx.controller;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisStreamReader;
import com.tinyx.service.SocialService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@Startup
@ApplicationScoped
public class RedisStreamPostSocial extends RedisStreamReader<PostQuery> {
    @Inject
    SocialService service;
    public RedisStreamPostSocial(){super();}

    public RedisStreamPostSocial(final ReactiveRedisDataSource ds)
    {
        super(ds, PostQuery.class,"repo-social", RedisChannel.POST);
    }

    @Override
    public void process(List<PostQuery> data)
    {
        List<PostContract> creation = new ArrayList<>();
        List<PostContract> deletion = new ArrayList<>();

        for(var i = 0; i<data.size();i++)
        {
            if(data.get(i).operation== PostQuery.Operation.CREATE)
                creation.add(data.get(i).post);
            else if(data.get(i).operation == PostQuery.Operation.DELETE)
                deletion.add(data.get(i).post);
        }
        service.createPost(creation);
        service.deletePost(deletion);
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
