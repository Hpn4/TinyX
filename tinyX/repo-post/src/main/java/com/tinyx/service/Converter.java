package com.tinyx.service;

import com.tinyx.repository.UserRepository;
import com.tinyx.repository.entity.Media;
import com.tinyx.repository.entity.Post;
import com.tinyx.repository.entity.User;

public class Converter {
    Post convertPost(com.tinyx.controller.contract.Post contract) {
        Post entity = new Post(contract.id, contract.userId, contract.content, contract.creationDate, contract.parentId, contract.mediaId);
        entity.likes = contract.likes;
        return entity;
    }

    com.tinyx.controller.contract.Post convertPost(Post entity) {
        com.tinyx.controller.contract.Post contract = new com.tinyx.controller.contract.Post(entity.id, entity.userId, entity.content, entity.creationDate, entity.parentId, entity.mediaId);
        contract.likes = entity.likes;
        return contract;
    }

    User convertUser(com.tinyx.controller.contract.User contract) {
        return new User(contract.id, contract.userName, contract.creationDate);
    }

    com.tinyx.controller.contract.User convertUser(User entity) {
        return new com.tinyx.controller.contract.User(entity.id, entity.userName, entity.creationDate);
    }

    Media convertMedia(com.tinyx.controller.contract.Media contract) {
        return null;
    }

    com.tinyx.controller.contract.Media convertMedia(Media entity) {
        return null;
    }
}
