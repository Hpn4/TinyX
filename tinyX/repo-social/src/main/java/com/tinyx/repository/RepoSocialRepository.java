package com.tinyx.repository;

import com.tinyx.repository.entity.Post;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class RepoSocialRepository {
    @Inject
    Driver neo4jDriver;

    public int CreatePost(Post post)
    {
        final var Session = neo4jDriver.session();
        String query = "CREATE (p:Post {id: "+post.Id.toString()+"})";
        final var createdLike = Session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        Session.close();
        return createdLike;
    }

    public int DeletePost(UUID id)
    {
        final var session = neo4jDriver.session();

        String query = "MATCH (n:Post {id: "+id.toString()+"}) DELETE n";
        final var deletedPost = session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        session.close();
        return deletedPost;
    }

    public int CreateLike(UUID UserId, UUID PostId)
    {
        final var session = neo4jDriver.session();

        String query = "CREATE (u:User {id:"+UserId.toString()+"})-[:LIKE {creationTime:'"+ Instant.now().toString()+"'}]->(p:Post {id:"+PostId+"})";
        final var createdLike = session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        session.close();
        return createdLike;

    }

    public int DeleteLike(UUID UserId, UUID PostId)
    {
        final var session = neo4jDriver.session();

        String query = "MATCH (u:User {id:"+UserId.toString()+"})-[r:LIKE]->(p:Post {id:"+PostId+"}) DELETE r";
        final var createdLike = session.executeWrite(tx -> tx.run(query)
                .consume().counters().relationshipsCreated());
        session.close();
        return createdLike;
    }
}
