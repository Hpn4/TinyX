package com.tinyx.redis.post;

import java.time.LocalDate;
import java.util.UUID;

public class User {
    public UUID id;
    public String userName;
    public LocalDate creationDate;

    public User(UUID id, String userName, LocalDate creationDate) {

        this.id = id;
        this.userName = userName;
        this.creationDate = creationDate;
    }
}
