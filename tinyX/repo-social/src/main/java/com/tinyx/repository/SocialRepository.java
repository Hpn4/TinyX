package com.tinyx.repository;

import com.tinyx.controller.UserSubscriber;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jboss.logging.Logger;
import org.neo4j.driver.Driver;

@ApplicationScoped
public class SocialRepository {
  @Inject Driver neo4jDriver;

  Logger log = Logger.getLogger(UserSubscriber.class);

  public void createPosts(List<UUID> posts) {
    if (posts.isEmpty()) return;

    String query = """
        UNWIND $posts AS post
        MERGE (:Post {id: post.id});
    """;

    List<Map<String, String>> postParams =
        posts.stream().map(id -> Map.of("id", id.toString())).toList();

    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("posts", postParams)));
      log.info("%d Posts created: ".formatted(posts.size()) + posts);
    } catch (Exception e) {
      log.info("Failed to create %d posts: ".formatted(posts.size()) + posts);
    }
  }

  public void deletePosts(List<UUID> posts) {

    String query =
        """
            UNWIND $posts AS post
            MATCH (n:Post {id: post.id}) DELETE n;
            """;
    List<Map<String, String>> postParams =
        posts.stream().map(id -> Map.of("id", id.toString())).toList();

    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("posts", postParams)));
      log.info("Successfully deleted %d posts: ".formatted(posts.size()) + posts);
    } catch (Exception e) {
      log.info("Failed to delete %d posts: ".formatted(posts.size()) + posts);
    }
  }

  public void createUsers(List<UUID> ids) {
    if (ids.isEmpty()) return;

    String query = """
        UNWIND $users AS user
        MERGE (:User {id: user.id});
    """;

    List<Map<String, String>> userParams =
        ids.stream().map(id -> Map.of("id", id.toString())).toList();

    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("users", userParams)));
      log.info("Succesfully created %d users: ".formatted(ids.size()) + ids);
    }
  }

  public void deleteUsers(List<UUID> users) {

    String query =
        """
            UNWIND $users AS user
            MATCH (n:User {id: user.id}) DELETE n;
            """;
    List<Map<String, String>> userParams =
        users.stream().map(id -> Map.of("id", id.toString())).toList();

    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("users", userParams)));
      log.info("Successfully deleted %d users: ".formatted(users.size()) + users);
    }
  }

  public void createRelations(
      List<SocialRelationEntity> relations, String relation, String t1, String t2) {
    String query =
        """
            UNWIND $relations AS relation
            MATCH (a:%s {id: relation.srcId})
            MATCH (b:%s {id: relation.targetId})
            WHERE NOT (a)-[:%s]->(b)
            MERGE (a)-[:%s {creation_time: relation.instant}]->(b);
            """
            .formatted(t1, t2, relation, relation);
    List<Map<String, String>> relationParams =
        relations.stream()
            .map(
                r ->
                    Map.of(
                        "srcId",
                        r.srcId.toString(),
                        "targetId",
                        r.targetId.toString(),
                        "instant",
                        r.timestamp.toString()))
            .toList();
    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("relations", relationParams)));
      log.info("Successfully created %d %s relations".formatted(relations.size(), relation));
    }
  }

  public void deleteRelations(
      List<SocialRelationEntity> relations, String relation, String type1, String type2) {
    String query =
        """
            UNWIND $relations AS relation
            MATCH (:%s {id: relation.srcId})-[r:%s]->(:%s {id: relation.targetId})
              DELETE r
            """
            .formatted(type1, relations, type2);
    List<Map<String, String>> relationParams =
        relations.stream()
            .map(r -> Map.of("srcId", r.srcId.toString(), "targetId", r.targetId.toString()))
            .toList();
    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("relations", relationParams)));
      log.info("Successfully deleted %d %s relations.".formatted(relations.size(), relation));
    }
  }
}
