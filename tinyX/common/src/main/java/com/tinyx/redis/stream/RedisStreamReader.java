package com.tinyx.redis.stream;

import static java.util.Collections.emptyList;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.stream.*;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.Cancellable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.jboss.logging.Logger;

public abstract class RedisStreamReader<T> {

  /* The number of ms to wait before getting a new batch of messages */
  private static final long DELAY_BETWEEN_READS = 500;

  /* When claiming, get messages that have been pending during at least CLAIM_IDLE_DELAY */
  private static final long CLAIM_IDLE_DELAY = 500;

  private static final Logger log = Logger.getLogger(RedisStreamReader.class);

  private String STREAM_GROUP;

  private String STREAM;

  private String STREAM_CONSUMER;

  private ReactiveStreamCommands<String, String, T> stream;

  private Cancellable consumer;

  public RedisStreamReader() {}

  public RedisStreamReader(
      final ReactiveRedisDataSource ds,
      final Class<T> c,
      final String group,
      final RedisChannel channel) {
    stream = ds.stream(c);

    STREAM = channel.toString();
    STREAM_GROUP = group;
    STREAM_CONSUMER = STREAM_GROUP + UUID.randomUUID();
  }

  @PostConstruct
  public void init() {
    consumer =
        stream
            .xgroupCreate(STREAM, STREAM_GROUP, "0", new XGroupCreateArgs().mkstream())
            .onFailure()
            .recoverWithNull()
            .map(v -> stream.xgroupCreateConsumer(STREAM, STREAM_GROUP, STREAM_CONSUMER))
            .map(v -> createStreamListener())
            .subscribe()
            .with(cancellable -> consumer = cancellable);
  }

  @PreDestroy
  public void destroy() {
    consumer.cancel();
    stream
        .xgroupDelConsumer(STREAM, STREAM_GROUP, STREAM_CONSUMER)
        .subscribe()
        .with(
            unack ->
                log.infof(
                    "[%s][%s][%s] Deleted with %d unacknowledged messages",
                    STREAM, STREAM_GROUP, STREAM_CONSUMER, unack));
  }

  private Cancellable createStreamListener() {
    log.info("Creating stream listener");

    XReadGroupArgs args = new XReadGroupArgs().count(-1);

    return Multi.createBy()
        .repeating()
        .uni(
            () ->
                stream
                    .xreadgroup(STREAM_GROUP, STREAM_CONSUMER, STREAM, ">", args)
                    .onFailure()
                    .invoke(
                        e ->
                            log.errorf(
                                "[%s][%s][%s] Error while reading",
                                e, STREAM, STREAM_GROUP, STREAM_CONSUMER))
                    .onFailure()
                    .recoverWithItem(emptyList()))
        .withDelay(Duration.ofMillis(DELAY_BETWEEN_READS))
        .indefinitely()
        .map(this::processMessage)
        .onFailure()
        .recoverWithItem(
            e -> {
              log.error("Cannot process message", e);
              return new String[0];
            })
        .map(this::acknowledge)
        .onItem()
        .transformToUniAndConcatenate(Function.identity())
        .subscribe()
        .with(count -> log.debugf("Collected %d requests", count));
  }

  public abstract void process(List<T> data);

  private String[] processMessage(List<StreamMessage<String, String, T>> messages) {
    if (messages == null || messages.isEmpty()) return new String[0];

    List<String> messageIds = new ArrayList<>();
    List<T> payloads = new ArrayList<>();

    for (StreamMessage<String, String, T> message : messages) {
      messageIds.add(message.id());
      payloads.add(message.payload().get(RedisPublisher.STREAM_KEY));
    }

    process(payloads);

    return messageIds.toArray(String[]::new);
  }

  private Uni<Integer> acknowledge(String[] ids) {
    return ids.length > 0 ? this.stream.xack(STREAM, STREAM_GROUP, ids) : Uni.createFrom().item(0);
  }



  protected void trimStream() {
    stream
        .xtrim(STREAM, new XTrimArgs().maxlen(10000).nearlyExactTrimming())
        .onFailure()
        .invoke(e -> log.errorf("[%s] Cannot trim stream", e, STREAM))
        .onFailure()
        .recoverWithItem(0L)
        .subscribe()
        .with(count -> log.infof("[%s] Trimmed %d messages", STREAM, count));
  }

  protected void claimPendingMessages() {
    stream
        .xclaim(STREAM, STREAM_GROUP, STREAM_CONSUMER, Duration.ofMillis(CLAIM_IDLE_DELAY), "0")
        .onFailure()
        .invoke(
            e ->
                log.errorf(
                    "[%s][%s][%s] Error while claiming", e, STREAM, STREAM_GROUP, STREAM_CONSUMER))
        .onFailure()
        .recoverWithItem(emptyList())
        .map(this::processMessage)
        .onFailure()
        .recoverWithItem(e -> new String[0])
        .map(this::acknowledge)
        .onItem()
        .transformToUni(Function.identity())
        .subscribe()
        .with(
            count ->
                log.debugf(
                    "[%s][%s][%s] Collected %d requests",
                    STREAM, STREAM_GROUP, STREAM_CONSUMER, count));
  }


}
