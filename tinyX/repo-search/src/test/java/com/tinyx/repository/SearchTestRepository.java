package com.tinyx.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tinyx.search.entity.SearchPostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SearchTestRepository {

  @Inject ElasticsearchClient esClient;

  @ConfigProperty(name = "tinyx.elasticsearch.index")
  String indexName;

  @Inject Logger log;

  /**
   * Returns all indexed post
   *
   * @return A list of all indexed posts
   */
  public List<SearchPostEntity> searchAllPostsEntity() {
    try {
      final SearchResponse<SearchPostEntity> response =
          esClient.search(
              s -> s.index(indexName).query(q -> q.matchAll(m -> m)), // Match all query
              SearchPostEntity.class);

      return response.hits().hits().stream().map(Hit::source).filter(Objects::nonNull).toList();
    } catch (final ElasticsearchException | IOException e) {
      log.errorf(e, "Error while fetching all posts");
    }

    return new ArrayList<>();
  }

  /**
   * Return all indexed posts as a list of UUID
   *
   * @return A list of UUID of indexed pots
   */
  public List<UUID> searchAllPosts() {
    return searchAllPostsEntity().stream().map(e -> e.postId).toList();
  }

  public void deleteAllPosts() {
    try {
      esClient.deleteByQuery(d -> d.index(indexName).query(q -> q.matchAll(m -> m)));
    } catch (final ElasticsearchException | IOException e) {
      log.errorf(e, "Error while deleting all posts from index: %s", indexName);
    }
  }
}
