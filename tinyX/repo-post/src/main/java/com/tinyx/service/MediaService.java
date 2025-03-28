package com.tinyx.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.tinyx.controller.contract.Media;
import com.tinyx.repository.MediaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class MediaService {
    @Inject
    MediaRepository mediaRepository;

    public UUID uploadMedia(Media media) {
        return null;
    }
    public Media getMedia(UUID id) {
        return null;
    }
}
