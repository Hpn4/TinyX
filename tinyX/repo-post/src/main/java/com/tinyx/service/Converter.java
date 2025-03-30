package com.tinyx.service;

import com.tinyx.repository.entity.Media;
import com.tinyx.repository.entity.Post;
import com.tinyx.repository.entity.User;

public class Converter {
    Post convertPost(com.tinyx.redis.post.Post contract) {
        Post entity = new Post(contract.id, contract.userId, contract.content, contract.creationDate, contract.parentId, contract.mediaId);
        entity.likes = contract.likes;
        return entity;
    }

    com.tinyx.redis.post.Post convertPost(Post entity) {
        com.tinyx.redis.post.Post contract = new com.tinyx.redis.post.Post(entity.id, entity.userId, entity.content, entity.creationDate, entity.parentId, entity.mediaId);
        contract.likes = entity.likes;
        return contract;
    }

    User convertUser(com.tinyx.redis.post.User contract) {
        return new User(contract.id, contract.userName, contract.creationDate);
    }

    com.tinyx.redis.post.User convertUser(User entity) {
        return new com.tinyx.redis.post.User(entity.id, entity.userName, entity.creationDate);
    }

    Media convertMedia(com.tinyx.redis.post.Media contract) {
        return null;
    }

    com.tinyx.redis.post.Media convertMedia(Media entity) {
        return null;
    }
}
