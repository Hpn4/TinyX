package com.tinyx.service;

import com.tinyx.media.contracts.MediaContract;
import com.tinyx.media.converter.MediaContractToMediaEntityConverter;
import com.tinyx.media.entity.MediaEntity;
import com.tinyx.post.contracts.PostContract;
import com.tinyx.repository.MediaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MediaService {
  @Inject MediaRepository mediaRepository;

  @Inject MediaContractToMediaEntityConverter mediaContractToMediaEntityConverter;

  /**
   * Allows to upload a media to the database (asynchronously).
   *
   * @param media The media information: its newly generated ID and the data stream.
   */
  public void uploadMedia(MediaContract media) {
    MediaEntity mediaEntity = mediaContractToMediaEntityConverter.convert(media);

    mediaRepository.uploadMedia(mediaEntity);
  }

  /**
   * The handler for CREATE Redis post queries. This will modify the metadata of medias to
   * incorporate a backreference to the post that has them.
   *
   * @param postContracts The post queries to handle. The posts inside CAN have null medias.
   */
  public void handleCreatePost(List<PostContract> postContracts) {
    HashMap<UUID, ArrayList<UUID>> updates = new HashMap<>();

    // Creating a map of posts to link to each media to limit the number of operations done.
    for (PostContract post : postContracts) {
      updates.computeIfAbsent(post.mediaId, mId -> new ArrayList<>());
      updates.get(post.mediaId).add(post.id);
    }

    mediaRepository.setMediaPosts(updates);
  }

  /**
   * The handler for DELETE Redis post queries. This will delete all medias linked to the given
   * posts.
   *
   * @param postContracts The post queries to handle. The posts inside CAN have null medias.
   */
  public void handleDeletePost(List<PostContract> postContracts) {
    for (PostContract post : postContracts) {
      if (post.mediaId != null) mediaRepository.removePost(post.mediaId, post.id);
    }
  }
}
