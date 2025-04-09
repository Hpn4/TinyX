package com.tinyx.repository;

import com.tinyx.repository.entity.SocialRelationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RelationsRepository {

  @Inject SocialRepository socialRepository;

  private void createRelations(final String query, final List<SocialRelationEntity> relations) {
    final List<Map<String, String>> relationParams =
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

    socialRepository.safeWrite(
        query, Map.of("relations", relationParams), "create relations", relations.size());
  }

  public void createLikeRelations(final List<SocialRelationEntity> relations) {
    final String query =
        """
        UNWIND $relations AS relation
          MATCH (u:User {id: relation.srcId})
          MATCH (p:Post {id: relation.targetId})
          MATCH (owner:User {id: p.authorId})
        WHERE
          NOT (u)-[:LIKE]->(p) AND
          NOT (u)-[:BLOCK]->(owner)
        MERGE (u)-[l:LIKE]->(p)
          ON CREATE SET l.creation_time = relation.instant;
        """;

    createRelations(query, relations);
  }

  public void createFollowRelations(final List<SocialRelationEntity> relations) {
    final String query =
        """
        UNWIND $relations AS relation
          MATCH (src:User {id: relation.srcId})
          MATCH (target:User {id: relation.targetId})
        WHERE
          NOT (src)-[:FOLLOW]->(target) AND
          NOT (src)-[:BLOCK]->(target) AND
          NOT (target)-[:BLOCK]->(src)
        MERGE (src)-[f:FOLLOW]->(target)
          ON CREATE SET f.creation_time = relation.instant;
        """;

    createRelations(query, relations);
  }

  public void createBlockRelations(final List<SocialRelationEntity> relations) {
    final String query =
        """
        UNWIND $relations AS relation
          MATCH (src:User {id: relation.srcId})
          MATCH (target:User {id: relation.targetId})
        WHERE
          NOT (src)-[:BLOCK]->(target)
        MERGE (src)-[b:BLOCK]->(target)
          ON CREATE SET b.creation_time = relation.instant;
        """;

    createRelations(query, relations);
  }

  public void deleteRelations(
      final List<SocialRelationEntity> relations,
      final String relation,
      final String type1,
      final String type2) {
    final String query =
        """
        UNWIND $relations AS relation
        MATCH (:%s {id: relation.srcId})-[r:%s]->(:%s {id: relation.targetId})
        DELETE r
        """
            .formatted(type1, relation, type2);

    final List<Map<String, String>> relationParams =
        relations.stream()
            .map(r -> Map.of("srcId", r.srcId.toString(), "targetId", r.targetId.toString()))
            .toList();

    socialRepository.safeWrite(
        query, Map.of("relations", relationParams), "delete relations", relations.size());
  }
}
