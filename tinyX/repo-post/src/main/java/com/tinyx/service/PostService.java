package com.tinyx.service;

import com.tinyx.redis.post.Post;
import com.tinyx.repository.PostRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PostService {
    @Inject
    PostRepository postRepository;

    public void createPost(Post post) {}
    public UUID deletePost(UUID id) { return null; }
    public List<Post> queryPostsList(List<UUID> postIds) { return null; }
    public List<Post> queryUserPosts(UUID userId) {
        return null;
    }
    public Post querySpecificPost(UUID id) { return null; }
    // I don't feel like this function is really necessary, you can use the updatePost system for that matter
    public void updatePostLikes(UUID id, int count) {}
    // I feel like replies can be gathered using the above list endpoint and neo4j, so is this function really useful ?
    public List<Post> queryPostReplies(UUID id) {
        return null;
    }
    public void updatePost(Post post) {}
}
