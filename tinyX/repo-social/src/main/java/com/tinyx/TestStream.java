package com.tinyx;

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

    @Scheduled(every = "10s")
    public void publish() {
        var user = factory.<UserQuery>createPublisher();

        UserContract contract = new UserContract(UUID.randomUUID(), "bro", LocalDate.now());
        UserQuery q = new UserQuery(UserQuery.Operation.CREATE, contract);

        for (var i = 0; i < 10; i++) {
            user.publishStream(RedisChannel.USER, q, UserQuery.class);
        }

        logger.info("Published 10x " + contract.id.toString());
    }
}
