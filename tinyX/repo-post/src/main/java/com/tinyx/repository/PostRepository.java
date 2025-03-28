package com.tinyx.repository;

import com.tinyx.repository.entity.Post;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepositoryBase<Post, UUID> {
    public PostRepository() {}

    /**
     * Create a new post
     * @param post post to be created
     */
    public void createPost(Post post) {}

    /**
     * Delete a specific post
     * @param id id of the post to delete
     * @return the id of the deleted post
     */
    public UUID deletePost(UUID id) {
        return null;
    }

    /**
     * Get all posts associated with a user
     * @param userId User to gather post from
     * @return All the posts
     */
    public List<Post> queryUserPost(UUID userId) {
        return null;
    }

    /**
     * Get a specific post
     * @param id Post id
     * @return The query post. Maybe null
     */
    public Post querySpecificPost(UUID id) {
        return null;
    }

    /**
     * changes the like number associated to a post
     * @param id Post to update
     * @param count Number of likes to add
     */
    public void updatePostLikes(UUID id, int count) {}

    /**
     * Get all the post associated to a post
     * @param id Post from which we gather replies
     * @return List of replies
     */
    public List<Post> queryPostReplies(UUID id) {
        return null;
    }

    // OPTIONAL: not specified in subject
    public void updatePost(Post post) {}



}