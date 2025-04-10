package com.tinyx;

import static org.neo4j.driver.Values.parameters;

import jakarta.inject.Inject;
import java.util.UUID;
import org.neo4j.driver.*;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

public class SrvcSocialRepositoryTests {

  @Inject Driver neo4jDriver;

  public void createUserAndPost(UUID userId, UUID postId) {

    // DEADCODE HERE, DON'T FORGET TO REMOVE
    /*
    neo4jDriver =
        GraphDatabase.driver(
            "bolt://localhost:7687",
            AuthTokens.basic("neo4j", "neo4jneo4j"));
       */
    try (Session session = neo4jDriver.session()) {

      String userQuery = "CREATE (u:User {id: $userId, name: 'Alice Smith'})";
      String postQuery =
          "CREATE (p:Post {id: $postId, content: 'This is a post from Alice', isBlocked: 'false'}) "
              + "MERGE (u:User {id: $userId}) "
              + "CREATE (u)-[:POSTED]->(p)";

      session.writeTransaction(
          tx -> {
            tx.run(userQuery, parameters("userId", userId.toString())); // Insère l'utilisateur
            tx.run(
                postQuery,
                parameters(
                    "userId",
                    userId.toString(),
                    "postId",
                    postId.toString())); // Insère le post et le lie
            return null;
          });

      System.out.println("[TEST] User and post created successfully!");
    } catch (Exception e) {
      System.err.println("[TEST] Error creating user and post: " + e.getMessage());
    }
  }

  public void deleteUserAndPost() {
    // neo4jDriver =
    //    GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jneo4j"));

    try (Session session = neo4jDriver.session()) {
      session.executeWrite(
          tx -> {
            tx.run("MATCH (n) DETACH DELETE n");
            return null;
          });
      System.out.println("[TEST] Toutes les données ont été supprimées !");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      neo4jDriver.close();
    }
  }
}
