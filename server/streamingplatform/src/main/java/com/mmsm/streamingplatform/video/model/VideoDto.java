package com.mmsm.streamingplatform.video.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class VideoDto {
    private Long id;
    private String title;
    private String description;
    private String createdById;
    private LocalDateTime createdDate;
}
