package com.mmsm.streamingplatform.video.videorating.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoRatingDto {

    private Long viewCount;
    private Long shareCount;
    private Long upVoteCount;
    private Long downVoteCount;
}
