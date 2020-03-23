package com.mmsm.streamingplatform.video.videorating.service;

import com.mmsm.streamingplatform.video.videorating.model.VideoRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRatingRepository extends JpaRepository<VideoRating, Long> {
    @Query(
            value = "SELECT * FROM video_rating WHERE video_id = :videoId AND created_by_id = :ownerId",
            nativeQuery = true
    )
    Optional<VideoRating> findVideoRatingByVideoIdAndUserId(Long videoId, String ownerId);
}
