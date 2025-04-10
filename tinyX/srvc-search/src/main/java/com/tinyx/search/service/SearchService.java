package com.tinyx.search.service;

import com.tinyx.ErrorCodes;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.search.repository.PostServiceClient;
import com.tinyx.search.repository.SearchRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

@ApplicationScoped
public class SearchService {

  @RestClient PostServiceClient postServiceClient;

  @Inject SearchRepository searchRepository;

  /**
   * Returns a list of PostContract from a list of UUID. Also handles errors in case of network
   * failures.
   *
   * @param userId The authenticated user used to filter out posts authored from blocked users
   * @param postsIds The posts to get the Contract
   * @return A list of PostContract
   * @throws jakarta.ws.rs.WebApplicationException with a status code of 503 in case of network
   *     failure
   */
  public List<PostContract> getPosts(final UUID userId, final List<UUID> postsIds) {
    try {
      return postServiceClient.queryPostsList(userId, postsIds);
    } catch (ClientWebApplicationException e) {
      ErrorCodes.UNREACHABLE.throwError("srvc-post");
    }

    return null;
  }

  /**
   * Performs a search query on Elasticsearch based on a given phrase and a list of hashtags.
   *
   * @param phrase A user-entered search string.
   * @param hashtags A list of hashtags to filter by (can be empty).
   * @return A list of matching {@link PostContract} objects containing the full post data.
   * @throws jakarta.ws.rs.WebApplicationException with status code 400 if the UUID is not in a
   *     valid format
   * @throws jakarta.ws.rs.WebApplicationException with status code 503 if ElasticSearch is
   *     unreachable/request error
   */
  public List<PostContract> searchPost(final UUID userId, String phrase, List<String> hashtags) {
    if (userId == null) ErrorCodes.WRONG_UUID.throwError("userId");

    // Filter out empty hashtags
    if (hashtags != null)
      hashtags = hashtags.stream().filter(h -> h != null && !h.isBlank()).toList();

    if ((phrase == null || phrase.isBlank()) && (hashtags == null || hashtags.isEmpty()))
      ErrorCodes.SEARCH_NO_QUERY.throwError();

    final List<UUID> posts = searchRepository.searchPost(phrase, hashtags);

    if (posts == null) ErrorCodes.UNREACHABLE.throwError("ElasticSearch");

    return getPosts(userId, posts);
  }
}
