package com.tinyx.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

@ApplicationScoped
public class LookupRepository {

  @Inject Driver neo4jDriver;

  @Inject Logger log;

  public enum BulkReadStatus {
    USERS_NOT_FOUND,
    BLOCKED,
    NO_BLOCK
  };

  /**
   * Executes a read-only query on the database and returns the result as a list of records.
   *
   * @param query the query string to execute.
   * @return a list of records resulting from the query (or null if error/empty).
   */
  public List<Record> executeRead(final String query) {
    try (Session session = neo4jDriver.session()) {
      return session.executeRead(
          tx -> {
            Result res = tx.run(query);
            if (!res.hasNext()) return null;
            return res.stream().toList();
          });
    } catch (Exception e) {
      log.errorf(e, "Error while executing query: %s", query);
    }

    return null;
  }

  /**
   * Retrieves the number of like received by a given post.
   *
   * @param postId the ID of the post to get the number of likes.
   * @return Integer of total number of likes (or 0 if error).
   */
  public Integer getNumberOfLike(final UUID postId) {
    final String cypherQuery =
        """
        MATCH (u:User)-[:LIKE]->(p:Post {id: "%s"})
        RETURN count(*) as likeCount
        """
            .formatted(postId);

    final List<Record> result = executeRead(cypherQuery);
    if (result == null) return 0;

    return result.get(0).get("likeCount").asInt();
  }

  /**
   * Retrieves the authorID for a given post.
   *
   * @param postId the ID of the post to retrieve the authorID from.
   * @return UUID of the author (or null if error).
   */
  public UUID getUserIdFromPost(final UUID postId) {
    final String cypherQuery =
        """
        MATCH (p:Post {id: "%s"})
        RETURN p.authorId AS authorId
        """
            .formatted(postId);

    final List<Record> result = executeRead(cypherQuery);
    if (result == null) return null;

    return UUID.fromString(result.get(0).get("authorId").asString());
  }

  /**
   * Checks if both users exist in the database and if there is a block relationship between the
   * source user and the other user
   *
   * @param userId The source user
   * @param authorId The other user
   * @return BulkReadStatus indicating the relationship status (not found, blocked, no block).
   */
  public BulkReadStatus checkUsersExistAndNoBlock(final UUID userId, final UUID authorId) {
    final String cypherQuery =
        """
        MATCH (user:User {id: "%s"})
        MATCH (target:User {id: "%s"})
        OPTIONAL MATCH (user)-[r:BLOCK]->(target)
        RETURN
          r IS NOT NULL AS isBlocked
        """
            .formatted(userId, authorId);

    // Will returns nothing if userId or authorId is null
    final List<Record> result = executeRead(cypherQuery);
    if (result == null) return BulkReadStatus.USERS_NOT_FOUND;

    if (result.get(0).get("isBlocked").asBoolean()) return BulkReadStatus.BLOCKED;

    return BulkReadStatus.NO_BLOCK;
  }

  /**
   * Returns whether a relation exist between the source user and the target user.
   *
   * @param srcId The source user
   * @param targetId The target user
   * @param relation Either BLOCK or LIKE
   * @return true if the relation exist false otherwise
   */
  public boolean checkRelationsExist(final UUID srcId, final UUID targetId, final String relation) {
    final String cypherQuery =
        """
        MATCH (user:User {id: "%s"})-[:%s]->(target:User {id: "%s"})
        RETURN true
        """
            .formatted(srcId, relation, targetId);

    final List<Record> result = executeRead(cypherQuery);

    // Will returns nothing if userId or authorId is null
    return result != null;
  }

  /**
   * Returns whether the given user already liked a post
   *
   * @param srcId The source userID
   * @param targetId The target postID
   * @return true if user already like, false otherwise
   */
  public boolean checkLikeExist(final UUID srcId, final UUID targetId) {
    final String cypherQuery =
        """
        MATCH (user:User {id: "%s"})-[:LIKE]->(target:Post {id: "%s"})
        RETURN true
        """
            .formatted(srcId, targetId);

    final List<Record> result = executeRead(cypherQuery);

    // Will returns nothing if userId or authorId is null
    return result != null;
  }
}
