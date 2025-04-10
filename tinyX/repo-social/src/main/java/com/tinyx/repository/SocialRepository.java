package com.tinyx.repository;

import com.tinyx.controller.UserSubscriber;
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

  Logger log = Logger.getLogger(UserSubscriber.class);

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

  public List<UUID> getLikersId(final UUID postId) {
    final String query =
        """
        MATCH (u:User)-[:LIKE]->(:Post {id: $postId})
        RETURN u.id AS uuid
        """;

    return readUUID(query, Values.parameters("postId", postId.toString()));
  }

  public List<UUID> getPostIdsFromUser(final UUID likerId, final UUID authorId) {
    final String query =
        """
        MATCH (:User {id: $likerId})-[:LIKE]->(p:Post {authorId: $authId})
        RETURN p.id AS uuid
        """;

    return readUUID(query, Values.parameters("authId", authorId, "likerId", likerId));
  }

  public int isNodeThere(List<UUID> ids, String nodeName) {
    final String query =
        """
            UNWIND nodeIds AS nodeId
            MATCH (n:%s {id: $nodeId})
            RETURN n.id AS uuid
            """
            .formatted(nodeName);
    final List<Map<String, String>> userParams =
        ids.stream().map(id -> Map.of("nodeId", id.toString())).toList();

    Session session = neo4jDriver.session();
    return session
        .executeRead(
            tx -> {
              final Result result = tx.run(query, Map.of("nodeIds", userParams));

              return result.stream()
                  .map(record -> UUID.fromString(record.get("uuid").asString()))
                  .collect(Collectors.toList());
            })
        .size();
  }
}
