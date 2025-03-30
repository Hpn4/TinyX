package com.tinyx.redis;

/**
 * Represents and stores REDIS channels for better and more consistent use
 * throughout the app.
 */
public enum RedisChannel {
    POST("post"),
    USER("user"),
    UPLOAD_MEDIA("media");

    private final String channel;

    RedisChannel(String channel) {
        this.channel = channel;
    }

    /**
     *
     * @return The String channel this enum represents
     * @see Enum#toString()
     */
    @Override
    public String toString() { return this.channel; }
}
