package com.mmsm.streamingplatform.video.mapper;

import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDto;

public class VideoMapper {

    public static VideoDto fromMovie(Video video) {
        return VideoDto.builder()
                .id(video.getId())
                .path(video.getPath())
                .build();
    }
}
