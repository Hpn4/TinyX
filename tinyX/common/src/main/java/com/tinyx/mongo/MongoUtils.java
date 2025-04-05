package com.tinyx.mongo;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MongoUtils {

  @Inject Logger logger;

  public <T> Optional<BulkWriteResult> BulkWriteOperations(
      List<WriteModel<T>> operations, MongoCollection<T> collection) {
    if (operations.isEmpty()) {
      logger.error("No operations to bulk write");
      return Optional.empty();
    }

    BulkWriteResult result;

    try {
      result = collection.bulkWrite(operations, new BulkWriteOptions().ordered(false));
    } catch (MongoBulkWriteException e) {
      logger.error("Unexpected bulk write errors (Updating): " + e.getMessage());
      return Optional.empty();
    }

    return Optional.of(result);
  }

  public <T> Optional<BulkWriteResult> Insert(Stream<T> elements, MongoCollection<T> collection) {
    return BulkWriteOperations(
        elements.map(e -> (WriteModel<T>) new InsertOneModel<T>(e)).toList(), collection);
  }

  public <E, T> List<E> Find(String field, List<T> values, MongoCollection<E> collection) {
    return values.stream()
        .flatMap(
            value -> collection.find(Filters.eq(field, value)).into(new ArrayList<>()).stream())
        .collect(Collectors.toList());
  }
}
