package com.tinyx;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.PostQuery;
import com.tinyx.redis.UserQuery;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.redis.stream.RedisPublisherFactory;
import com.tinyx.user.contracts.UserContract;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
@Startup
public class TestStream {

  @Inject RedisPublisherFactory factory;

  Logger logger = Logger.getLogger(TestStream.class);

  UUID unique = UUID.randomUUID();
  UUID uniquepost = UUID.randomUUID();
  UUID second = UUID.randomUUID();

  @Scheduled(every = "10s")
  public void publish() {
    var user = factory.<UserQuery>createPublisher();

    UserContract contract = new UserContract(unique, "bro", ZonedDateTime.now());
    UserQuery q = new UserQuery(UserQuery.Operation.CREATE, contract);

    // UserContract secondContract = new UserContract(second, "dude", LocalDate.now());
    // UserQuery q2 = new UserQuery(UserQuery.Operation.CREATE, secondContract);
    var post = factory.<PostQuery>createPublisher();

    PostContract pc =
        new PostContract(
            uniquepost,
            contract.id,
            "sup bro",
            ZonedDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID());
    PostQuery pq = new PostQuery(PostQuery.Operation.CREATE, pc);

    /*PostContract delpc = new PostContract(pc.id,contract.id,"nop bro",LocalDate.now(),UUID.randomUUID(),UUID.randomUUID());
    PostQuery delpq = new PostQuery(PostQuery.Operation.DELETE, delpc);*/

    /*  var like = factory.<LikePostQuery>createPublisher();

        LikePostQuery lpq = new LikePostQuery(LikePostQuery.Operation.LIKE, unique, uniquepost, ZonedDateTime.now());
        LikePostQuery unlpq = new LikePostQuery(LikePostQuery.Operation.UNLIKE, unique, uniquepost,ZonedDateTime.now());

        var realtion = factory.<UserRelationsQuery>createPublisher();

        UserRelationsQuery blurq =
            new UserRelationsQuery(UserRelationsQuery.Operation.BLOCK, unique, UUID.randomUUID(),ZonedDateTime.now());
        UserRelationsQuery unblurq =
            new UserRelationsQuery(UserRelationsQuery.Operation.UNBLOCK, unique, second,ZonedDateTime.now());

        UserRelationsQuery folurq =
            new UserRelationsQuery(UserRelationsQuery.Operation.FOLLOW, unique, UUID.randomUUID(),ZonedDateTime.now());
        UserRelationsQuery unfolurq =
            new UserRelationsQuery(UserRelationsQuery.Operation.UNFOLLOW, unique, second,ZonedDateTime.now());
    */
    for (var i = 0; i < 10; i++) {
      user.publishStream(RedisChannel.USER, q, UserQuery.class);
      //  user.publishStream(RedisChannel.USER, q2, UserQuery.class);
      post.publishStream(RedisChannel.POST, pq, PostQuery.class);
      // post.publishStream(RedisChannel.POST,delpq, PostQuery.class);
      // like.publishStream(RedisChannel.SOCIAL,lpq,LikePostQuery.class);
      /// like.publishStream(RedisChannel.SOCIAL,unlpq, LikePostQuery.class);
      // realtion.publishStream(RedisChannel.SOCIAL, folurq, UserRelationsQuery.class);
      //  realtion.publishStream(RedisChannel.SOCIAL,unfolurq,UserRelationsQuery.class);

    }

    logger.info("Published 10x " + contract.id.toString());

    /* var post = factory.<PostQuery>createPublisher();
    PostContract pc = new PostContract(UUID.randomUUID(),contract.id,"sup bro",LocalDate.now(),UUID.randomUUID(),UUID.randomUUID());
    PostQuery pq = new PostQuery(PostQuery.Operation.CREATE, pc);*/

  }
}
