package com.tinyx.service;

import com.tinyx.converter.PostQueryToSearchPostEntity;
import com.tinyx.redis.PostQuery;
import com.tinyx.repository.SearchRepository;
import com.tinyx.search.entity.SearchPostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SearchService {

  @Inject PostQueryToSearchPostEntity postQueryToSearchPostEntity;

  @Inject SearchRepository searchRepository;

  /**
   * Indexes a list of PostQueries in one go.
   *
   * @param queries the list of PostQueries to be indexed
   */
  public void indexPosts(final List<PostQuery> queries) {
    final List<SearchPostEntity> entities = postQueryToSearchPostEntity.convert(queries);

    searchRepository.indexPosts(entities);
  }

  /**
   * Deletes multiple documents in one go.
   *
   * @param ids the list of {@link UUID} document identifiers to delete
   */
  public void deletePosts(final List<UUID> ids) {
    searchRepository.deletePosts(ids);
  }
}
