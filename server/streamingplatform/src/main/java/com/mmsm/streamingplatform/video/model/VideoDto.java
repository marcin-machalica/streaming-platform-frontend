package com.mmsm.streamingplatform.video.model;

import lombok.Builder;

import java.util.Date;

@Builder
public class VideoDto {

    private Long id;
    private String path;
    private Date created_at;
}
