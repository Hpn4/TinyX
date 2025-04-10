package com.tinyx.converter;

import com.tinyx.post.contracts.PostContract;
import com.tinyx.redis.PostQuery;
import com.tinyx.search.entity.SearchPostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class PostQueryToSearchPostEntity {

  private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

  /**
   * Extracts unique hashtags from the given text. Hashtags are normalized to lowercase and returned
   * without the '#' symbol.
   *
   * @param text The text to extract hashtags from.
   * @return A list of distinct, lowercase hashtags.
   */
  private List<String> extractHashtags(final String text) {
    final Matcher matcher = HASHTAG_PATTERN.matcher(text);
    return matcher
        .results()
        .map(match -> match.group(1).toLowerCase())
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * Converts a {@link PostQuery} to a {@link SearchPostEntity}, extracting hashtags from the
   * content.
   *
   * @param post The post query to convert.
   * @return A {@link SearchPostEntity} containing the post ID, content, and extracted hashtags.
   */
  public SearchPostEntity convert(final PostQuery post) {
    final PostContract postContract = post.post;

    return new SearchPostEntity(
        postContract.id, postContract.content, extractHashtags(postContract.content));
  }

  public List<SearchPostEntity> convert(final List<PostQuery> queries) {
    return queries.stream().map(this::convert).collect(Collectors.toList());
  }
}
