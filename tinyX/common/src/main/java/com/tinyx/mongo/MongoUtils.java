package com.tinyx.mongo;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.tinyx.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import java.util.stream.Stream;
import org.bson.conversions.Bson;
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

  /**
   * Executes a bulk write on the given Mongo collection with optional error handling.
   *
   * @param operations The list of write operations to perform.
   * @param collection The Mongo collection to apply the operations to.
   * @param errorHandling A map defining how to handle specific bulk write errors.
   * @return Containing the result if the operation was successful (optional).
   */
  public <T> Optional<BulkWriteResult> bulkWriteOperations(
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

  /**
   * Performs a bulk write operation on the specified collection with default error handling.
   *
   * @param operations The list of write operations (insert, update, delete) to execute.
   * @param collection The MongoDB collection to apply the operations on.
   * @param <T> The type of documents in the collection.
   * @return Result of the bulk write if successful (optional).
   */
  public <T> Optional<BulkWriteResult> bulkWriteOperations(
      List<WriteModel<T>> operations, MongoCollection<T> collection) {
    return bulkWriteOperations(operations, collection, new HashMap<>());
  }

  /**
   * Inserts a stream of elements into the specified collection using a bulk write operation.
   *
   * @param elements The stream of elements to insert.
   * @param collection The MongoDB collection where the elements will be inserted.
   * @param <T> The type of documents in the collection.
   * @return An Optional containing the result of the bulk insert if successful, or empty otherwise.
   */
  public <T> Optional<BulkWriteResult> insert(Stream<T> elements, MongoCollection<T> collection) {
    return bulkWriteOperations(
        elements.map(e -> (WriteModel<T>) new InsertOneModel<T>(e)).toList(), collection);
  }

  /**
   * Inserts a stream of elements into the specified collection using a bulk write operation
   *
   * @param elements The stream of elements to insert.
   * @param collection The MongoDB collection where the elements will be inserted.
   * @param errorHandling A map defining how specific errors should be handled during the bulk
   *     operation.
   * @param <T> The type of documents in the collection.
   * @return An Optional containing the result of the bulk insert if successful, or empty if no
   *     operation was performed.
   */
  public <T> Optional<BulkWriteResult> insert(
      Stream<T> elements, MongoCollection<T> collection, Map<BWError, BWEAction> errorHandling) {
    return bulkWriteOperations(
        elements.map(e -> (WriteModel<T>) new InsertOneModel<T>(e)).toList(),
        collection,
        errorHandling);
  }

  /**
   * Removes multiple documents from the specified collection based on the provided field and
   * values. It performs a bulk delete operation using the specified field and list of values.
   *
   * @param field The field in the documents to match against.
   * @param values The list of values to match the specified field.
   * @param collection The MongoDB collection from which to remove the documents.
   * @param <T> The type of documents in the collection.
   * @param <V> The type of values used to match the specified field.
   * @return Result of the bulk delete operation if successful (optional)
   */
  public <T, V> Optional<BulkWriteResult> Remove(
      String field, List<V> values, MongoCollection<T> collection) {
    return bulkWriteOperations(
        List.of(new DeleteManyModel<T>(Filters.in(field, values))), collection);
  }

  /**
   * Finds documents in the specified collection where the value of a specified field matches any
   * value from a list of values.
   *
   * @param field The field in the documents to match against.
   * @param values The list of values to match the specified field.
   * @param collection The MongoDB collection to search in.
   * @param <E> The type of documents in the collection.
   * @param <T> The type of values used to match the specified field.
   * @return A list of documents that match the specified field and values.
   */
  public <E, T> List<E> find(String field, List<T> values, MongoCollection<E> collection) {
    return collection.find(Filters.in(field, values)).into(new ArrayList<>());
  }

  public <T> void handleMongoWriteOperationGeneric(
      HashMap<UUID, ArrayList<UUID>> map,
      Operation oper,
      String fieldName,
      MongoCollection<T> collection) {

    ArrayList<WriteModel<T>> operations = new ArrayList<>();

    for (Map.Entry<UUID, ArrayList<UUID>> entry : map.entrySet()) {
      ArrayList<UUID> values = entry.getValue();

      if (values == null || values.isEmpty()) {
        logger.warn("No elements found for " + entry.getKey() + ", unexpected behavior.");
        continue;
      }

      Bson filter = Filters.eq("_id", entry.getKey());
      Bson update =
          oper == Operation.ADD
              ? Updates.addEachToSet(fieldName, values)
              : Updates.pullAll(fieldName, values);

      operations.add(new UpdateOneModel<>(filter, update));
    }

    bulkWriteOperations(operations, collection);
  }
}
