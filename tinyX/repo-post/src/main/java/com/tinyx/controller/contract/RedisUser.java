package com.tinyx.controller.contract;

public class RedisUser {
    public enum Operation {
        CREATE,
        UPDATE,
        // Potentially add delete here
    }

    public Operation operation;
    public User user;

    public RedisUser(Operation operation, User user) {
        this.operation = operation;
        this.user = user;
    }
}
