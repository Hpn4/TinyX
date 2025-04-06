package com.tinyx.repository;

import com.tinyx.controller.SocialSubscriberUser;
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

  Logger log = Logger.getLogger(SocialSubscriberUser.class);
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
      log.info("Posts created :"+posts);
    }
    catch (Exception e)
    {
      log.info("Failed to create posts: "+posts);
    }
  }

  public void deletePost(List<UUID> posts) {

    String query =
        """
            UNWIND $posts AS post
            MATCH (n:Post {id: post.id}) DELETE n;
            """;
    List<Map<String, String>> postParams =
        posts.stream().map(id -> Map.of("id", id.toString())).toList();

    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("posts", postParams)));
      log.info("Succesfully deleted posts: "+posts);
    }
    catch (Exception e)
    {
      log.info("Failed to delete posts: "+posts);
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
      log.info("Succesfully created users: "+ids);
    }
    catch (Exception e)
    {
      log.info("Failed to create users: "+ids);
    }
  }

  public void deleteUser(List<UUID> users) {

    String query =
        """
            UNWIND $users AS user
            MATCH (n:User {id: user.id}) DELETE n;
            """;
    List<Map<String, String>> userParams =
        users.stream().map(id -> Map.of("id", id.toString())).toList();

    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("users", userParams)));
      log.info("Successfully deleted users: "+users);
    }
    catch (Exception e)
    {
      log.info("Failed to delete users: "+users);
    }
  }

  public void createRelation(
      List<SocialRelationEntity> relations, String relation, String t1, String t2) {
    String query =
        """
            UNWIND $relations AS relation
            MATCH (a:"""
            + t1
            + """
             {id: relation.srcId})
            MATCH (b: """
            + t2
            + """
             {id: relation.targetId})
            WHERE NOT (a)-[:"""
            + relation
            + """
            ]->(b)
            MERGE (a)-[:"""
            + relation
            + """
             {creation_time: relation.instant}]->(b);
            """;
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
      log.info("Successfully created relation "+relation+" between "+t1+": "+"and "+t2);
    }
    catch(Exception e)
    {
      log.info("Failed to create relation "+relation+" between "+t1+": "+"and "+t2);
    }
  }

  public void deleteRelation(
      List<SocialRelationEntity> relations, String relation, String type1, String type2) {
    String query =
        """
            UNWIND $relations AS relation
            MATCH (:"""
            + type1
            + """
             {id: relation.srcId})-[r:"""
            + relation
            + """
            ]->(:"""
            + type2
            + """
             {id: relation.targetId})
              DELETE r
            """;
    List<Map<String, String>> relationParams =
        relations.stream()
            .map(r -> Map.of("srcId", r.srcId.toString(), "targetId", r.targetId.toString()))
            .toList();
    try (var session = neo4jDriver.session()) {
      session.executeWrite(tx -> tx.run(query, Map.of("relations", relationParams)));
      log.info("Successfully deleted relation "+relation+" between "+type1+": "+"and "+type2);
    }
    catch(Exception e)
    {
      log.info("Failed to create relation "+relation+" between "+type1+": "+"and "+type2);
    }
  }
}
