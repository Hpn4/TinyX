package com.tinyx.repository;

import com.tinyx.repository.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.neo4j.driver.*;

@ApplicationScoped
public class SocialRepository {
  @Inject Driver neo4jDriver;

  @Inject Logger log;

  /**
   * Execute a query with parameters in order to retrieve a list ids.
   *
   * @param query query to execute.
   * @param parameter parameters to use with the query.
   */
  protected List<UUID> readUUID(final String query, final Value parameter) {
    try (Session session = neo4jDriver.session()) {
      return session.executeRead(
          tx -> {
            final Result result = tx.run(query, parameter);

            return result.stream()
                .map(record -> UUID.fromString(record.get("uuid").asString()))
                .collect(Collectors.toList());
          });
    } catch (Exception e) {
      log.errorf("Failed to read UUIDs, queries: %s", e, query);
    }

    return new ArrayList<>();
  }

  /**
   * Safely add or delete nodes from the database using a query.
   *
   * @param query query to execute
   * @param parameters elements to modify
   * @param message name of the operation done
   * @param count number of elements to modify
   */
  protected void safeWrite(
      final String query,
      final Map<String, Object> parameters,
      final String message,
      final int count) {

    try (Session session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, parameters));
      log.infof("Successfully %s: %d", message, count);
    } catch (Exception e) {
      log.errorf("Failed to %s: %d", e, message, count);
    }
  }

  /**
   * Create Post nodes in the neo4j.
   *
   * @param posts list of posts to create
   */
  public void createPosts(final List<PostEntity> posts) {
    final String query =
        """
        UNWIND $posts AS post
        MERGE (:Post {id: post.id, authorId: post.authorId});
        """;

    final List<Map<String, String>> postParams =
        posts.stream()
            .map(post -> Map.of("id", post.id.toString(), "authorId", post.authorId.toString()))
            .toList();

    safeWrite(query, Map.of("posts", postParams), "create posts", posts.size());
  }

  /**
   * Delete Post nodes in the neo4j.
   *
   * @param posts list of posts to delete
   */
  public void deletePosts(final List<PostEntity> posts) {
    final String query =
        """
        UNWIND $posts AS post
        MATCH (n:Post {id: post.id})
        DETACH DELETE n;
        """;

    final List<Map<String, String>> postParams =
        posts.stream().map(post -> Map.of("id", post.id.toString())).toList();

    safeWrite(query, Map.of("posts", postParams), "delete posts", posts.size());
  }

  /**
   * Create User nodes in the neo4j .
   *
   * @param ids list of user's id to create
   */
  public void createUsers(final List<UUID> ids) {
    final String query =
        """
        UNWIND $users AS user
        MERGE (:User {id: user.id});
        """;

    final List<Map<String, String>> userParams =
        ids.stream().map(id -> Map.of("id", id.toString())).toList();

    safeWrite(query, Map.of("users", userParams), "create users", ids.size());
  }

  /**
   * Get the ids of all the users that as like a post.
   *
   * @param postId id of the post that the users has liked
   * @return the list of id of the users that liked the post
   */
  public List<UUID> getLikersId(final UUID postId) {
    final String query =
        """
        MATCH (u:User)-[:LIKE]->(:Post {id: $postId})
        RETURN u.id AS uuid
        """;

    return readUUID(query, Values.parameters("postId", postId.toString()));
  }

  /**
   * get the ids of all the posts from a user that another user has liked
   *
   * @param authorId author of the liked posts
   * @param likerId user that has liked posts from the author
   * @return The list of ids of the posts authored by the author and liked by the liker
   */
  public List<UUID> getPostIdsFromUser(final UUID likerId, final UUID authorId) {
    final String query =
        """
        MATCH (:User {id: $likerId})-[:LIKE]->(p:Post {authorId: $authId})
        RETURN p.id AS uuid
        """;

    return readUUID(
        query, Values.parameters("authId", authorId.toString(), "likerId", likerId.toString()));
  }
}
