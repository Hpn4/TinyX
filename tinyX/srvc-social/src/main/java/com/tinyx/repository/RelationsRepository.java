package com.tinyx.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.neo4j.driver.Record;

@ApplicationScoped
public class RelationsRepository {

  @Inject LookupRepository lookupRepository;

  private List<UUID> getResultAsList(final String cipher) {
    final List<Record> result = lookupRepository.executeRead(cipher);
    if (result == null) return List.of();

    return result.stream().map(rec -> UUID.fromString(rec.get("id").asString())).toList();
  }

  /**
   * Returns the list of user UUIDs that has liked the post given in parameter. Blocked users are
   * filtered out
   *
   * @param postId The post to retrieve the list of likers from
   * @param userId The user used to filter out blocked users
   * @return The list of user UUIDs
   */
  public List<UUID> getLikers(final UUID postId, final UUID userId) {
    final String cipher =
        """
        MATCH (viewer:User {id: "%s"}), (u:User)-[:LIKE]->(p:Post {id: "%s"})
        WHERE NOT (viewer)-[:BLOCKED]->(u)
        RETURN u.id as id
        """
            .formatted(userId, postId);

    return getResultAsList(cipher);
  }

  /**
   * Returns the list of post UUIDs that has been liked by the given user. Posts authored by users
   * blocked by the authenticated user are filtered out.
   *
   * @param targetId The user to retrieve the list of liked post form
   * @param userId The user used to filter out blocked users
   * @return The list of liked posts UUIDs
   */
  public List<UUID> getLikedPosts(final UUID targetId, final UUID userId) {
    final String cipher =
        """
        MATCH (viewer:User {id: "%s"}), (u:User {id: "%s"})-[:LIKE]->(p:Post)
        WITH viewer, p, p.authorId AS authorId
        WHERE NOT EXISTS {
          MATCH (viewer)-[:BLOCKED]->(author:User)
          WHERE author.id = authorId
        }
        RETURN p.id as id
        """
            .formatted(userId, targetId);

    return getResultAsList(cipher);
  }

  /**
   * Returns the list of user UUIDs that the given user follows. Users blocked by the authenticated
   * user are filtered out.
   *
   * @param targetId The user to retrieve his follow list
   * @param userId The user used to filter out blocked users
   * @return The list of user UUIDs
   */
  public List<UUID> getUserFollow(final UUID targetId, final UUID userId) {
    final String cipher =
        """
        MATCH (viewer:User {id: "%s"}), (u:User {id: "%s"})-[:FOLLOW]->(v:User)
        WHERE NOT (viewer)-[:BLOCKED]->(v)
        RETURN v.id as id
        """
            .formatted(userId, targetId);

    return getResultAsList(cipher);
  }

  /**
   * Returns the list of user UUIDs that follow the given user. Users blocked by the authenticated
   * user are filtered out.
   *
   * @param targetId The user to retrieve his followers from
   * @param userId The user used to filter out blocked users
   * @return The list of user UUIDs
   */
  public List<UUID> getFollowers(final UUID targetId, final UUID userId) {
    final String cipher =
        """
        MATCH (viewer:User {id: "%s"}), (u:User)-[:FOLLOW]->(v:User {id: "%s"})
        WHERE NOT (viewer)-[:BLOCKED]->(u)
        RETURN u.id as id"""
            .formatted(userId, targetId);

    return getResultAsList(cipher);
  }

  /**
   * Returns the list of user UUIDs that has been blocked by the given users. Users blocked by the
   * authenticated user are filtered out.
   *
   * @param targetId The user to retrieve his blocked list from
   * @param userId The user used to filter out blocked users
   * @return The list of user UUIDs
   */
  public List<UUID> getBlockedUsers(final UUID targetId, final UUID userId) {
    final String cipher =
        """
        MATCH (viewer:User {id: "%s"}), (u:User {id: "%s"})-[:BLOCK]->(v:User)
        WHERE NOT (viewer)-[:BLOCKED]->(v)
        RETURN v.id as id"""
            .formatted(userId, targetId);

    return getResultAsList(cipher);
  }

  /**
   * Returns the list of user UUIDs who blocked the given user. Users blocked by the authenticated
   * user are filtered out.
   *
   * @param targetId The target user
   * @param userId The user used to filter out blocked users
   * @return The list of user UUIDs
   */
  public List<UUID> getTargetBlock(final UUID targetId, final UUID userId) {
    final String cipher =
        """
        MATCH (viewer:User {id: "%s"}), (u:User)-[:BLOCK]->(v:User {id: "%s"})
        WHERE NOT (viewer)-[:BLOCKED]->(u)
        RETURN u.id as id"""
            .formatted(userId, targetId);

    return getResultAsList(cipher);
  }
}
