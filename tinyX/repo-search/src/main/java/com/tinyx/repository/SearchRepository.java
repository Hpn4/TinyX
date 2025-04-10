package com.tinyx.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.tinyx.search.entity.SearchPostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
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
   * Small utility function to execute a bulk operations. In case of errors log them with the reason
   *
   * @param bulkRequest The bulk request to execute
   */
  private void execBulk(final BulkRequest bulkRequest) {
    try {
      final BulkResponse result = esClient.bulk(bulkRequest);

      if (result.errors()) {
        result
            .items()
            .forEach(
                item -> {
                  if (item.error() != null)
                    log.errorf("Error in Bulk Request: %s", item.error().reason());
                });
      }
    } catch (final ElasticsearchException | IOException e) {
      log.errorf(e, "Error while executing bulk request");
    }
  }

  /**
   * Indexes a list of SearchPostEntity objects into Elasticsearch using a bulk request.
   *
   * @param entities the list of SearchPostEntity to be indexed
   */
  public void indexPosts(final List<SearchPostEntity> entities) {
    final BulkRequest.Builder br = new BulkRequest.Builder();

    for (final SearchPostEntity post : entities) {
      br.operations(
          op -> op.index(idx -> idx.index(indexName).id(post.postId.toString()).document(post)));
    }

    execBulk(br.build());
  }

  /**
   * Deletes multiple documents from the Elasticsearch index using a bulk request.
   *
   * @param ids the list of {@link UUID} document identifiers to delete
   */
  public void deletePosts(final List<UUID> ids) {
    final BulkRequest.Builder br = new BulkRequest.Builder();

    // Add each operation to the Bulk
    for (final UUID id : ids) {
      br.operations(op -> op.delete(d -> d.index(indexName).id(id.toString())));
    }

    execBulk(br.build());
  }
}
