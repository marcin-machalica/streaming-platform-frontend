package com.mmsm.streamingplatform.video.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class VideoDto {

    private Long id;
    private String path;
    private String createdBy;
    private LocalDateTime createdDate;
}
