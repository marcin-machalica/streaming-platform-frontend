package com.mmsm.streamingplatform.video.videorating.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoRatingDto {
    private Boolean isUpVote;
    private Boolean isDownVote;
}
