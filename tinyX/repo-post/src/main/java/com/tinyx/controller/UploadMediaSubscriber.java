package com.tinyx.controller;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.tinyx.media.contracts.MediaContract;
import com.tinyx.redis.stream.RedisChannel;
import com.tinyx.service.MediaService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import java.util.function.Consumer;
import org.jboss.logging.Logger;

public class UploadMediaSubscriber implements Consumer<MediaContract> {
  @Inject MediaService service;

  @Inject Logger logger;

  private final PubSubCommands.RedisSubscriber subscriber;

  public UploadMediaSubscriber(final RedisDataSource ds) {
    subscriber =
        ds.pubsub(MediaContract.class).subscribe(RedisChannel.UPLOAD_MEDIA.toString(), this);
  }

  @Override
  public void accept(final MediaContract message) {
    // To keep things simple, we will avoid asynchronous stuff here,
    // so you need to tell Quarkus that you will execute blocking
    // code knowingly, otherwise it may crash at runtime to prevent
    // unwanted blocking code.
    vertx.executeBlocking(
        future -> {
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
