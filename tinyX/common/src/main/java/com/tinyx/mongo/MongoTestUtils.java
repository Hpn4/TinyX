package com.tinyx.mongo;

import com.mongodb.client.MongoCollection;
import jakarta.ejb.DuplicateKeyException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class MongoTestUtils {

  @Inject MongoUtils mongoUtils;

  private int TIME_SETUP = 1000;
  private int TIME_BETWEEN_RETRIES = 100;
  private int NUMBER_OF_RETRIES = 10;

  /**
   * Tests if elements with given field values exist in the Mongo collection.
   *
   * @param field The field to filter on.
   * @param values The list of values to search for.
   * @param unique True if each value should match only one element.
   * @param collection The MongoDB collection to search.
   */
  public <E, T> void testFind(
      String field, List<T> values, boolean unique, MongoCollection<E> collection)
      throws NotFoundException, DuplicateKeyException, InterruptedException {
    Thread.sleep(TIME_SETUP);

    int tries = 0;

    for (int j = 0; j < values.size(); j++) {
      T value = values.get(j);

      List<E> result = mongoUtils.find(field, Collections.singletonList(value), collection);

      if (result.isEmpty()) {
        if (tries == NUMBER_OF_RETRIES)
          throw new NotFoundException(
              "Could not find element with field: " + field + " and value: " + value);
        else {
          Thread.sleep(TIME_BETWEEN_RETRIES);
          j--;
          tries++;
          continue;
        }
      }

      if (unique && result.size() > 1)
        throw new DuplicateKeyException(
            "More than one element found with field: " + field + " and value: " + value);

      tries = 0;
    }
  }
}
