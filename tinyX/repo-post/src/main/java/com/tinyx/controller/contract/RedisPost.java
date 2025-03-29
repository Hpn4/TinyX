package com.tinyx.controller.contract;

public class RedisPost {
    public enum Operation {
        CREATE,
        DELETE,
        UPDATE,
    }

    public Operation operation;
    public Post post;

    public RedisPost(Operation operation, Post post) {
        this.operation = operation;
        this.post = post;
    }
}
