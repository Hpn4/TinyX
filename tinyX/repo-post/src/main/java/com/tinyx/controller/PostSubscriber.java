package com.tinyx.controller;

import com.tinyx.redis.RedisChannel;
import com.tinyx.redis.post.PostQuery;
import com.tinyx.service.PostService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.function.Consumer;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

public class PostSubscriber implements Consumer<PostQuery> {
    @Inject
    PostService service;

    @Inject
    Logger logger;

    private final PubSubCommands.RedisSubscriber subscriber;
    public PostSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(PostQuery.class).subscribe(RedisChannel.POST.toString(), this);
    }
    @Override
    public void accept(final PostQuery message) {
        // To keep things simple, we will avoid asynchronous stuff here,
        // so you need to tell Quarkus that you will execute blocking
        // code knowingly, otherwise it may crash at runtime to prevent
        // unwanted blocking code.
        vertx.executeBlocking(future -> {
            // dispatch the message to service-layer methods here
            if (message == null || message.post == null) {
                logger.error("Post message or post itself is null");
            }
            else if (message.operation == PostQuery.Operation.CREATE) {
                service.createPost(message.post);
            } else if (message.operation == PostQuery.Operation.DELETE) {
                service.deletePost(message.post.id);
            } else if (message.operation == PostQuery.Operation.UPDATE) {
                service.updatePost(message.post);
            } else {
                logger.error("Unknown post operation " + message.operation);
            }

            future.complete();
        });
    }
    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
