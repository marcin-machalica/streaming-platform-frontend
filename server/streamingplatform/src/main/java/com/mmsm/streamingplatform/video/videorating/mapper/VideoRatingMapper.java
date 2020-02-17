package com.mmsm.streamingplatform.video.videorating.mapper;

import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.videorating.model.VideoRating;
import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;

public class VideoRatingMapper {

    public static VideoRatingDto getVideoRatingDtoFromEntity(Video entity) {
        if (entity == null) {
            return null;
        }
        VideoRating videoRating = entity.getVideoRating();

        return VideoRatingDto.builder()
                .viewCount(videoRating != null ? videoRating.getViewCount() : null)
                .shareCount(videoRating != null ? videoRating.getShareCount() : null)
                .upVoteCount(videoRating != null ? videoRating.getUpVoteCount() : null)
                .downVoteCount(videoRating != null ? videoRating.getDownVoteCount() : null)
                .build();
    }
}
