package com.tinyx.redis.post;

import java.io.InputStream;
import java.util.UUID;

public class Media {
    public UUID id;
    public InputStream data;

    public Media(UUID id, InputStream data) {
        this.id = id;
        this.data = data;
    }
}
