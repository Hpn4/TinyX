package com.tinyx;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.LikePostQuery;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import com.tinyx.user.contracts.UserContract;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
@Startup
public class TestStream {

    @Inject
    RedisPublisherFactory factory;

    Logger logger = Logger.getLogger(TestStream.class);

    UUID unique = UUID.randomUUID();
    UUID uniquepost = UUID.randomUUID();
    @Scheduled(every = "10s")
    public void publish() {
        var user = factory.<UserQuery>createPublisher();


        UserContract contract = new UserContract(unique, "bro", LocalDate.now());
        UserQuery q = new UserQuery(UserQuery.Operation.CREATE, contract);
        var post = factory.<PostQuery>createPublisher();

        PostContract pc = new PostContract(uniquepost,contract.id,"sup bro",LocalDate.now(),UUID.randomUUID(),UUID.randomUUID());
        PostQuery pq = new PostQuery(PostQuery.Operation.CREATE, pc);


        /*PostContract delpc = new PostContract(pc.id,contract.id,"nop bro",LocalDate.now(),UUID.randomUUID(),UUID.randomUUID());
        PostQuery delpq = new PostQuery(PostQuery.Operation.DELETE, delpc);*/

        var like = factory.<LikePostQuery>createPublisher();

        LikePostQuery lpq = new LikePostQuery(LikePostQuery.Operation.LIKE,unique,uniquepost);
        for (var i = 0; i < 10; i++) {
            user.publishStream(RedisChannel.USER, q, UserQuery.class);
            post.publishStream(RedisChannel.POST,pq, PostQuery.class);
            //post.publishStream(RedisChannel.POST,delpq, PostQuery.class);
            like.publishStream(Red);
        }

        logger.info("Published 10x " + contract.id.toString());

       /* var post = factory.<PostQuery>createPublisher();

        PostContract pc = new PostContract(UUID.randomUUID(),contract.id,"sup bro",LocalDate.now(),UUID.randomUUID(),UUID.randomUUID());
        PostQuery pq = new PostQuery(PostQuery.Operation.CREATE, pc);*/


    }
}
