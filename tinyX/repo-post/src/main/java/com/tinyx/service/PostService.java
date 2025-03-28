package com.tinyx.service;

import com.tinyx.controller.contract.Post;
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
    public UUID deletePost(UUID id) {
        return null;
    }
    public List<Post> queryUserPost(UUID userId) {
        return null;
    }
    public Post querySpecificPost(UUID id) { return null; }
    public void updatePostLikes(UUID id, int count) {}
    public List<Post> queryPostReplies(UUID id) {
        return null;
    }
    public void updatePost(com.tinyx.repository.entity.Post post) {}
}
