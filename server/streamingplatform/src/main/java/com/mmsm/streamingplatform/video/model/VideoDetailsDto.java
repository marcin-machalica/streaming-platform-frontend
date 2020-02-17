package com.mmsm.streamingplatform.video.model;

import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoDetailsDto {

    private VideoDto videoDto;
    private VideoRatingDto videoRatingDto;
}
