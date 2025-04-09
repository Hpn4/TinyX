package com.tinyx.repository;

import com.tinyx.controller.UserSubscriber;
import com.tinyx.repository.entity.PostEntity;
import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Values;

@ApplicationScoped
public class SocialRepository {
  @Inject Driver neo4jDriver;

  Logger log = Logger.getLogger(UserSubscriber.class);

  public void createPosts(List<PostEntity> posts) {
    if (posts.isEmpty()) return;

    String query =
        """
        UNWIND $posts AS post
        MERGE (:Post {id: post.id, authorId: post.authorId});
    """;

    List<Map<String, String>> postParams =
        posts.stream()
            .map(post -> Map.of("id", post.id.toString(), "authorId", post.authId.toString()))
            .toList();

    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("posts", postParams)));
      log.infof("%d Posts created", posts.size());
    } catch (Exception e) {
      log.errorf("Failed to create %d posts", e, posts.size());
    }
  }

  public void deletePosts(List<PostEntity> posts) {

    String query =
        """
            UNWIND $posts AS post
            MATCH (n:Post {id: post.id})
            DETACH DELETE n;
            """;
    List<Map<String, String>> postParams =
        posts.stream().map(post -> Map.of("id", post.id.toString())).toList();

    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("posts", postParams)));
      log.infof("%d Posts deleted", posts.size());
    } catch (Exception e) {
      log.errorf("Failed to delete %d posts", e, posts.size());
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
      log.infof("Successfully created %d users", ids.size());
    } catch (Exception e) {
      log.errorf("Failed to create %d users", e, ids.size());
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
      log.infof("Successfully created %d relations", relations.size());
    } catch (Exception e) {
      log.errorf("Failed to create %d relations", e, relations.size());
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
      log.infof("Successfully deleted %d relations", relations.size());
    } catch (Exception e) {
      log.errorf("Failed to delete %d relations", e, relations.size());
    }
  }

  public Boolean IsUserBlocked(UUID srcUserId, UUID blockedId) {
    var session = neo4jDriver.session();
    String query =
        """
            RETURN EXISTS {
            MATCH (:User {id: $srcId})-[:BLOCK]->(:User {id: $blockId}) LIMIT 1
            } AS IsBlocked
            """;

    return session.executeRead(
        tx -> {
          var result = tx.run(query, Values.parameters("srcId", srcUserId, "blockId", blockedId));
          if (result.hasNext()) return result.single().get("IsBlocked").asBoolean();
          else return false;
        });
  }

  public List<UUID> getLikersId(UUID postId) {
    String query =
        """
            MATCH (u:User)-[:LIKE]->(:Post {id: $postId})
            RETURN u.id AS uuid
            """;
    var session = neo4jDriver.session();
    List<UUID> r =
        session.executeRead(
            tx -> {
              Result result = tx.run(query, Values.parameters("postId", postId.toString()));

              return result.stream()
                  .map(record -> UUID.fromString(record.get("uuid").asString()))
                  .collect(Collectors.toList());
            });
    return r;
  }

  public UUID getPostAuthor(UUID postId) {
    String query =
        """
            MATCH (p:Post {id: $postId})
            RETURN p.authorId AS uuid
            """;

    var session = neo4jDriver.session();
    UUID r =
        session.executeRead(
            tx -> {
              Result result = tx.run(query, Values.parameters("postId", postId));

              return UUID.fromString(result.single().get("uuid").asString());
            });
    return r;
  }

  public List<UUID> getPostIdsFromUser(UUID likerId, UUID authorId) {
    String query =
        """
                MATCH (:User {id: $likerId})-[:LIKE]->(p:Post {authorId: $authId})
                RETURN p.id AS uuid""";

    var session = neo4jDriver.session();
    List<UUID> r =
        session.executeRead(
            tx -> {
              Result result =
                  tx.run(query, Values.parameters("authId", authorId, "likerId", likerId));

              return result.stream()
                  .map(record -> UUID.fromString(record.get("uuid").asString()))
                  .collect(Collectors.toList());
            });
    return r;
  }
}
