package com.tinyx.service;

import com.tinyx.ErrorCodes;
import com.tinyx.controller.request.CreatePostRequest;
import com.tinyx.converter.CreatePostRequestToPostContractConverter;
import com.tinyx.converter.UuidToPostContractConverter;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.post.converter.PostEntityToPostContractConverter;
import com.tinyx.post.entity.PostEntity;
import com.tinyx.post.enumeration.PostType;
import com.tinyx.redis.PostQuery;
import com.tinyx.repository.MediaRestClient;
import com.tinyx.repository.PostServiceRepository;
import com.tinyx.repository.UserRestClient;
import com.tinyx.repository.publisher.PostPublisher;
import com.tinyx.user.contracts.UserContract;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

/** Handle all request from PostController. Purely post related. */
@ApplicationScoped
public class PostService {

  @Inject PostServiceRepository postServiceRepository;

  @Inject CreatePostRequestToPostContractConverter createPostRequestToPostContractConverter;

  @Inject UuidToPostContractConverter uuidToPostContractConverter;

  @Inject PostEntityToPostContractConverter postEntityToPostContractConverter;

  @Inject PostPublisher postPublisher;

  @RestClient UserRestClient userRestClient;

  @RestClient MediaRestClient mediaRestClient;

  /**
   * Check if a reply or repost is allowed
   *
   * @param post: new post
   * @return int 0: true, 1: post not found, 2: post from blocked user
   */
  private Boolean isParentPostAuthorBlocked(UserContract user, CreatePostRequest post) {
    // Get the parent post
    PostEntity parentPost = getPostEntity(post.parentId);

    // Check if the author of the parent post is blocked
    return user.blockedUsers != null && user.blockedUsers.contains(parentPost.userId);
  }

  private PostEntity getPostEntity(UUID postUUID) {
    return postServiceRepository
        .findPost(postUUID)
        .orElseThrow(ErrorCodes.POST_NOT_FOUND.asSupplier(postUUID));
  }

  private UserContract getUser(UUID userId) {
    try {
      return userRestClient.getUserById(userId).readEntity(UserContract.class);
    } catch (ClientWebApplicationException e) {
      ErrorCodes.USER_NOT_FOUND.throwError(userId);
    }
    return null;
  }

  /**
   * Create a new post
   *
   * @param userUUID: user UUID
   * @param post: http contract from controller
   */
  public void newPost(UUID userUUID, CreatePostRequest post) {
    if (userUUID == null || post == null || post.content.length() > 160) {
      ErrorCodes.BAD_POST_FORMAT.throwError();
    }

    UserContract user = getUser(userUUID);

    if (post.mediaId != null && !mediaRestClient.doesMediaExistEndpoint(post.mediaId)) {
      ErrorCodes.MEDIA_NOT_FOUND.throwError(post.mediaId);
    }

    if (post.postType == PostType.REPLY || post.postType == PostType.REPOST) {
      if (post.parentId == null) {
        ErrorCodes.BAD_POST_FORMAT.throwError();
      }
      if (!postServiceRepository.existPost(post.parentId)) {
        ErrorCodes.POST_NOT_FOUND.throwError(post.parentId);
      }
      if (isParentPostAuthorBlocked(user, post)) {
        ErrorCodes.BLOCKED_USER_POST.throwError();
      }
      if (post.postType == PostType.REPOST && post.mediaId != null && !post.content.isBlank()) {
        ErrorCodes.BAD_POST_FORMAT.throwError();
      }
    }

    PostContract contract = createPostRequestToPostContractConverter.converter(post, userUUID);
    PostQuery postQuery = new PostQuery(PostQuery.Operation.CREATE, contract);

    postPublisher.publish(postQuery);
  }

  /**
   * Delete a post
   *
   * @param userUUID: user UUID
   * @param postUUID: ID of the post
   */
  public void deletePost(UUID userUUID, UUID postUUID) {
    if (userUUID == null || postUUID == null) {
      ErrorCodes.WRONG_UUID.throwError();
    }

    // Check if user exists
    getUser(userUUID);

    PostEntity post = getPostEntity(postUUID);

    if (!post.userId.equals(userUUID)) {
      ErrorCodes.FORBIDDEN_USER_ACTION.throwError(userUUID);
    }

    PostContract contract = uuidToPostContractConverter.converter(userUUID, postUUID);
    PostQuery postQuery = new PostQuery(PostQuery.Operation.DELETE, contract);

    postPublisher.publish(postQuery);
  }

  /**
   * Return a post thanks to the ID
   *
   * @param postId: ID of the post
   * @param userId: user UUID
   * @return rest response
   */
  public PostContract GetPostById(UUID postId, UUID userId) {
    if (postId == null || userId == null) {
      ErrorCodes.WRONG_UUID.throwError();
    }

    UserContract user = getUser(userId);

    PostEntity post = getPostEntity(postId);

    if (user.blockedUsers != null && user.blockedUsers.contains(post.userId)) {
      ErrorCodes.BLOCKED_USER_POST.throwError(userId);
    }
    return postEntityToPostContractConverter.converter(post);
  }

  /**
   * Return a list of post thanks to the given ID user
   *
   * @param postsIds: ID of the post
   * @param userId: user UUID
   * @return rest response
   */
  public List<PostContract> GetAllPost(List<UUID> postsIds, UUID userId) {
    if (postsIds == null || userId == null) {
      ErrorCodes.WRONG_UUID.throwError();
    }

    UserContract user = getUser(userId);

    return postServiceRepository.findPosts(postsIds, user.blockedUsers).stream()
        .map(postEntityToPostContractConverter::converter)
        .toList();
  }

  /**
   * Return all the post from a user
   *
   * @param authorId: post author UUID
   * @param userId: user UUID
   * @return rest response
   */
  public List<PostContract> GetAllPostsFromUser(UUID authorId, UUID userId) {
    if (authorId == null || userId == null) {
      ErrorCodes.WRONG_UUID.throwError();
    }

    UserContract user = getUser(userId);

    // Test if author exist
    UserContract author = getUser(authorId);

    if (user.blockedUsers != null && user.blockedUsers.contains(authorId)) {
      ErrorCodes.BLOCKED_USER_POST.throwError(authorId);
    }

    List<PostEntity> postEntities = postServiceRepository.findPosts(author.posts);

    return postEntities.stream().map(postEntityToPostContractConverter::converter).toList();
  }

  /**
   * Return the replies of a post
   *
   * @param postId: ID of the post
   * @param userId: user UUID
   * @return rest response
   */
  public List<PostContract> GetRepliesByPostId(UUID postId, UUID userId) {
    if (postId == null || userId == null) {
      ErrorCodes.WRONG_UUID.throwError();
    }

    List<UUID> replyIds = getPostEntity(postId).children;

    UserContract user = getUser(userId);

    return postServiceRepository.findPostsReply(replyIds, user.blockedUsers).stream()
        .map(postEntityToPostContractConverter::converter)
        .toList();
  }
}
