package com.mmsm.streamingplatform.video.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VideoDto {

    @JsonIgnoreProperties()
    private Long id;
    @JsonIgnoreProperties
    private String path;
}
