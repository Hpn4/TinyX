package com.tinyx.controller;

import com.tinyx.controller.contract.Media;
import com.tinyx.service.MediaService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.function.Consumer;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

public class UploadMediaSubscriber implements Consumer<Media> {
    @Inject
    MediaService service;

    @Inject
    Logger logger;

    private final PubSubCommands.RedisSubscriber subscriber;
    public UploadMediaSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(Media.class).subscribe("TODO", this);
    }
    @Override
    public void accept(final Media message) {
        // To keep things simple, we will avoid asynchronous stuff here,
        // so you need to tell Quarkus that you will execute blocking
        // code knowingly, otherwise it may crash at runtime to prevent
        // unwanted blocking code.
        vertx.executeBlocking(future -> {
            // dispatch the message to service-layer methods here
            if (message == null) {
                logger.error("Media message is null");
            } else {
                service.uploadMedia(message);
            }

            future.complete();
        });
    }
    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
