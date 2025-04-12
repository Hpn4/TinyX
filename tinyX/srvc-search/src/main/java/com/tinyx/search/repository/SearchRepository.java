package com.tinyx.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tinyx.search.entity.SearchPostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SearchRepository {

  @Inject ElasticsearchClient esClient;

  @ConfigProperty(name = "tinyx.elasticsearch.index")
  String indexName;

  @Inject Logger log;

  /**
   * Executes a search query using the provided boolean query and returns matching post IDs. Log in
   * case of error.
   *
   * @param boolQuery The boolean query to execute.
   * @return A list of post IDs matching the query, or {@code null} if an error occurs.
   */
  private List<UUID> searchPost(final BoolQuery boolQuery) {
    final Query query = Query.of(q -> q.bool(boolQuery));

    try {
      final SearchResponse<SearchPostEntity> document =
          esClient.search(s -> s.index(indexName).query(query).size(10000), SearchPostEntity.class);

      return document.hits().hits().stream()
          .map(Hit::source)
          .filter(Objects::nonNull)
          .map(d -> d.postId)
          .toList();
    } catch (final ElasticsearchException | IOException e) {
      log.errorf(e, "Error while searching post");
    }

    return null;
  }

  /**
   * Searches for posts matching the given phrase and hashtags. Both conditions are combined using a
   * boolean "must" (AND).
   *
   * @param phrase The text to match in the "text" field (optional).
   * @param hashtags List of hashtags to match in the "hashtags" field (optional).
   * @return A list of post UUIDs matching the criteria, or {@code null} if an error occurs.
   */
  public List<UUID> searchPost(final String phrase, final List<String> hashtags) {
    final BoolQuery.Builder build = new BoolQuery.Builder();

    if (phrase != null && !phrase.isBlank()) build.must(getWordQuery(phrase));

    if (hashtags != null && !hashtags.isEmpty()) build.must(getHashtagsQuery(hashtags));

    return searchPost(build.build());
  }

  private static Query getWordQuery(final String word) {
    return MatchQuery.of(m -> m.field("text").query(word))._toQuery();
  }

  /**
   * Builds a query that matches documents containing all the specified hashtags.
   *
   * @param hashtags List of hashtags to match.
   * @return A query requiring all hashtags to be present in the "hashtags" field.
   */
  private static Query getHashtagsQuery(final List<String> hashtags) {
    final BoolQuery.Builder boolQuery = new BoolQuery.Builder();

    for (final String hashtag : hashtags) {
      boolQuery.must(q -> q.term(t -> t.field("hashtags").value(hashtag)));
    }

    return boolQuery.build()._toQuery();
  }
}
