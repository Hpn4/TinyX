package com.tinyx.controller;

import com.tinyx.controller.contract.RedisUser;
import com.tinyx.service.UserService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.UUID;
import java.util.function.Consumer;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

public class UserSubscriber implements Consumer<RedisUser> {
    @Inject
    UserService service;

    @Inject
    Logger logger;

    private final PubSubCommands.RedisSubscriber subscriber;
    public UserSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(RedisUser.class).subscribe("TODO", this);
    }
    @Override
    public void accept(final RedisUser message) {
        // To keep things simple, we will avoid asynchronous stuff here,
        // so you need to tell Quarkus that you will execute blocking
        // code knowingly, otherwise it may crash at runtime to prevent
        // unwanted blocking code.
        vertx.executeBlocking(future -> {
            // dispatch the message to service-layer methods here
            if (message == null || message.user == null) {
                logger.error("User message or user itself is null");
            }
            else if (message.operation == RedisUser.Operation.CREATE) {
                service.createUser(message.user);
            }
            else if (message.operation == RedisUser.Operation.UPDATE) {
                service.updateUser(message.user);
            }
            else {
                logger.error("Unknown user operation: " + message.operation);
            }
            // Potentially add a delete user here

            future.complete();
        });
    }
    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
