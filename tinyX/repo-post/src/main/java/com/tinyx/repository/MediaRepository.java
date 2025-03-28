package com.tinyx.repository;

import com.mongodb.client.gridfs.GridFSBucket;
import com.tinyx.repository.entity.Media;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class MediaRepository implements PanacheMongoRepositoryBase<Media, UUID> {

    public MediaRepository() {}

    /**
     * Add a media to the database
     * @param media Media to upload
     * @return the UUID associated to the media: we will use it to create the UUID post
     */
    public UUID uploadMedia(GridFSBucket media) {
        return null;
    }

    /**
     * get a media associated to a post
     * @param id id of the Post/Media
     * @return the Media
     */
    public GridFSBucket getMedia(UUID id) {
        return null;
    }
}
