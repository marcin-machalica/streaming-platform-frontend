package com.mmsm.streamingplatform.video.videorating.mapper;

import com.mmsm.streamingplatform.video.videorating.model.VideoRating;
import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;

public class VideoRatingMapper {

    public static VideoRatingDto getVideoRatingDtoFromEntity(VideoRating videoRating) {
        if (videoRating == null) {
            return null;
        }

        return VideoRatingDto.builder()
            .isUpVote(videoRating.getIsUpVote())
            .isDownVote(videoRating.getIsDownVote())
            .build();
    }
}
