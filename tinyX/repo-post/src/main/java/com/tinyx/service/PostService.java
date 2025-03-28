package com.tinyx.service;

import com.tinyx.controller.contract.Post;
import com.tinyx.repository.MediaRepository;
import com.tinyx.repository.PostRepository;
import com.tinyx.repository.UserRepository;
import jakarta.inject.Inject;

import java.util.UUID;

public class PostService {
    @Inject
    PostRepository postRepository;

    public void createPost(Post post) {}
    public UUID deletePost(UUID id) {
        return null;
    }
}
