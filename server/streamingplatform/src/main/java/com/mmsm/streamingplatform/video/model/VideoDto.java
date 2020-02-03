package com.mmsm.streamingplatform.video.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VideoDto {

    private Long id;
    private String path;
}
