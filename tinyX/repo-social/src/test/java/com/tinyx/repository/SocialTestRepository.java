package com.tinyx.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

@ApplicationScoped
public class SocialTestRepository {

  @Inject Driver neo4jDriver;

  @Inject Logger log;

  public List<UUID> getResultAsList(final String query) {
    try (Session session = neo4jDriver.session()) {
      return session.executeRead(
          tx -> {
            Result res = tx.run(query);
            if (!res.hasNext()) return null;
            return res.stream()
                .map(rec -> UUID.fromString(rec.get("id").asString()))
                .sorted()
                .toList();
          });
    } catch (Exception e) {
      log.errorf(e, "Error while executing query: %s", query);
    }

    return null;
  }

  public void deleteAllData() {
    final String deleteQuery = "MATCH (n) DETACH DELETE n";
    try (Session session = neo4jDriver.session()) {
      session.executeWrite(
          tx -> {
            tx.run(deleteQuery);
            return null;
          });
    } catch (final Exception e) {
      log.errorf(e, "Failed to delete all data");
    }
  }

  public List<UUID> getAllUsers() {
    return getResultAsList("MATCH (u:User) RETURN u.id as id");
  }

  public List<UUID> getAllPosts() {
    return getResultAsList("MATCH (p:Post) RETURN p.id as id");
  }

  public List<UUID> getFollow(final UUID userId) {
    return getResultAsList(
        "MATCH (u:User {id: \"%s\"})-[:FOLLOW]->(v:User) RETURN v.id as id".formatted(userId));
  }

  public List<UUID> getFollowers(final UUID userId) {
    return getResultAsList(
        "MATCH (u:User)-[:FOLLOW]->(v:User {id: \"%s\"}) RETURN u.id as id".formatted(userId));
  }
}
