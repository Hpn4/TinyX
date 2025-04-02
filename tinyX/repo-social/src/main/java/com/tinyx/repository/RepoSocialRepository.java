package com.tinyx.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.neo4j.driver.Driver;

@ApplicationScoped
public class RepoSocialRepository {
  @Inject Driver neo4jDriver;

  public void CreatePost(String posts) {
    final var Session = neo4jDriver.session();

    String query = "MERGE " + posts;
    Session.executeWrite(tx -> tx.run(query).consume().counters().relationshipsCreated());
    Session.close();
  }

  public void DeletePost(String posts) {
    final var session = neo4jDriver.session();

    String query = "MATCH (n:Post) WHERE n.id IN " + posts + "DELETE n";
    session.executeWrite(tx -> tx.run(query).consume().counters().relationshipsCreated());
    session.close();
  }

  public void CreateUser(String users) {
    final var Session = neo4jDriver.session();
    String query = "MERGE " + users;
    Session.executeWrite(tx -> tx.run(query).consume().counters().relationshipsCreated());
    Session.close();
  }

  public void DeleteUser(String users) {
    final var session = neo4jDriver.session();
    String query = "MATCH (n:User) WHERE n.id IN " + users + " DELETE n";
    session.executeWrite(tx -> tx.run(query).consume().counters().relationshipsCreated());
    session.close();
  }

  public void CreateRelation(String relations) {
    final var session = neo4jDriver.session();

    String query = "MERGE " + relations;
    session.executeWrite(tx -> tx.run(query).consume().counters().relationshipsCreated());
    session.close();
  }

  public void DeleteRelation(
      String Ids1, String Ids2, String relation, String type1, String type2) {
    final var session = neo4jDriver.session();

    String query =
        "MATCH (t1:"
            + type1
            + ")-[r:"
            + relation
            + "]->(t2:"
            + type2
            + ") WHERE t1.id IN "
            + Ids1
            + " AND t2.id IN "
            + Ids2
            + " DELETE r";
    session.executeWrite(tx -> tx.run(query).consume().counters().relationshipsCreated());
    session.close();
  }
}
