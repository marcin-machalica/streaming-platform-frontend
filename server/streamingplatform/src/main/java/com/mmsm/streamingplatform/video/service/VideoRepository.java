package com.mmsm.streamingplatform.video.service;

import com.mmsm.streamingplatform.video.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

}
