package com.tinyx.mongo;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import java.util.stream.Stream;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MongoUtils {

  @Inject Logger logger;

  public enum BWError {
    DUPLICATE_KEY,
    EXECUTION_TIMEOUT,
    UNCATEGORIZED,
    MATCH_MISSING,
    INSERTS_MISSING,
    DELETIONS_MISSING,
    MODIFICATIONS_MISSING
  }

  public enum BWEAction {
    THROW,
    IGNORE
  }

  public BWError mongoErrorToBW(BulkWriteError error) {
    switch (error.getCategory()) {
      case DUPLICATE_KEY -> {
        return BWError.DUPLICATE_KEY;
      }
      case EXECUTION_TIMEOUT -> {
        return BWError.EXECUTION_TIMEOUT;
      }
      default -> {
        return BWError.UNCATEGORIZED;
      }
    }
  }

  public void errorHandlingDefaultBehavior(Map<BWError, BWEAction> errorHandling) {
    errorHandling.putIfAbsent(BWError.DUPLICATE_KEY, BWEAction.IGNORE);
    errorHandling.putIfAbsent(BWError.EXECUTION_TIMEOUT, BWEAction.THROW);
    errorHandling.putIfAbsent(BWError.UNCATEGORIZED, BWEAction.IGNORE);
    errorHandling.putIfAbsent(BWError.MATCH_MISSING, BWEAction.IGNORE);
    errorHandling.putIfAbsent(BWError.INSERTS_MISSING, BWEAction.IGNORE);
    errorHandling.putIfAbsent(BWError.DELETIONS_MISSING, BWEAction.IGNORE);
    errorHandling.putIfAbsent(BWError.MODIFICATIONS_MISSING, BWEAction.IGNORE);
  }

  public <T> Optional<BulkWriteResult> BulkWriteOperations(
      List<WriteModel<T>> operations,
      MongoCollection<T> collection,
      Map<BWError, BWEAction> errorHandling) {
    if (operations.isEmpty()) {
      logger.error("No operations to bulk write");
      return Optional.empty();
    }

    errorHandlingDefaultBehavior(errorHandling);

    BulkWriteResult result;

    try {
      result = collection.bulkWrite(operations, new BulkWriteOptions().ordered(false));
    } catch (MongoBulkWriteException e) {
      logger.error("Got bulk write errors:");
      e.getWriteErrors().forEach(we -> logger.error(we.getMessage()));

      var throwing =
          e.getWriteErrors().stream()
              .map(this::mongoErrorToBW)
              .filter(
                  we -> errorHandling.containsKey(we) && errorHandling.get(we) == BWEAction.THROW);

      if (throwing.findAny().isPresent())
        throw new RuntimeException(
            "An error was thrown here so that redis can potentially retry a failed write on another pod.");

      return Optional.empty();
    }

    if ((result.getMatchedCount() != operations.size()
            && errorHandling.getOrDefault(BWError.MATCH_MISSING, BWEAction.IGNORE)
                == BWEAction.THROW)
        || (result.getInsertedCount() != operations.size()
            && errorHandling.getOrDefault(BWError.INSERTS_MISSING, BWEAction.IGNORE)
                == BWEAction.THROW)
        || (result.getDeletedCount() != operations.size()
            && errorHandling.getOrDefault(BWError.DELETIONS_MISSING, BWEAction.IGNORE)
                == BWEAction.THROW)
        || (result.getModifiedCount() != operations.size()
            && errorHandling.getOrDefault(BWError.MODIFICATIONS_MISSING, BWEAction.IGNORE)
                == BWEAction.THROW))
      throw new RuntimeException(
          "An error was thrown here due to missing matches so that redis can potentially retry a failed write on another pod.");

    return Optional.of(result);
  }

  public <T> Optional<BulkWriteResult> BulkWriteOperations(
      List<WriteModel<T>> operations, MongoCollection<T> collection) {
    return BulkWriteOperations(operations, collection, new HashMap<>());
  }

  public <T> Optional<BulkWriteResult> Insert(Stream<T> elements, MongoCollection<T> collection) {
    return BulkWriteOperations(
        elements.map(e -> (WriteModel<T>) new InsertOneModel<T>(e)).toList(), collection);
  }

  public <T> Optional<BulkWriteResult> Insert(
      Stream<T> elements, MongoCollection<T> collection, Map<BWError, BWEAction> errorHandling) {
    return BulkWriteOperations(
        elements.map(e -> (WriteModel<T>) new InsertOneModel<T>(e)).toList(),
        collection,
        errorHandling);
  }

  public <T, V> Optional<BulkWriteResult> Remove(
      String field, List<V> values, MongoCollection<T> collection) {
    return BulkWriteOperations(
        List.of(new DeleteManyModel<T>(Filters.in(field, values))), collection);
  }

  public <E, T> List<E> Find(String field, List<T> values, MongoCollection<E> collection) {
    return collection.find(Filters.in(field, values)).into(new ArrayList<>());
  }
}
