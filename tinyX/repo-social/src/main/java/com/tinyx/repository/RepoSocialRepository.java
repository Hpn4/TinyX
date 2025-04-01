package com.tinyx.repository;

import com.tinyx.repository.entity.Post;
import com.tinyx.repository.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RepoSocialRepository {
    @Inject
    Driver neo4jDriver;

    public void CreatePost(Post post)
    {
        final var Session = neo4jDriver.session();
        String query = "MERGE (p:Post {id: "+post.Id.toString()+"})";
        Session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        Session.close();

    }


    public void DeletePost(UUID id)
    {
        final var session = neo4jDriver.session();

        String query = "MATCH (n:Post {id: "+id.toString()+"}) DELETE n";
        session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        session.close();

    }

    public void CreateUser(User user)
    {
        final var Session = neo4jDriver.session();
        String query = "MERGE (u:User {id: "+user.id.toString()+"})";
        Session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        Session.close();
    }

    public void DeleteUser(UUID id)
    {
        final var session = neo4jDriver.session();

        String query = "MATCH (n:User {id: "+id.toString()+"}) DELETE n";
        session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        session.close();

    }

    public void CreateRelation(UUID id1,UUID id2, String relation, String type1, String type2)
    {
        final var session = neo4jDriver.session();


        String query = "MERGE (u:"+type1+" {id:"+id1.toString()+"})-[:"+relation+" {creationTime:'"+ Instant.now().toString()+"'}]->(p:"+type2+" {id:"+id2+"})";
        session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        session.close();


    }

    public void DeleteRelation(UUID UserId, UUID PostId,String relation, String type1, String type2)
    {
        final var session = neo4jDriver.session();

        String query = "MATCH (u:"+type1+" {id:"+UserId.toString()+"})-[r:"+relation+"]->(p:"+type2+" {id:"+PostId+"}) DELETE r";
        session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        session.close();

    }


}
