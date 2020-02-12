package com.mmsm.streamingplatform.video.mapper;

import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDto;

public class VideoMapper {

    public static VideoDto getVideoDtoFromEntity(Video video) {
        if (video == null) {
            return null;
        }

        return VideoDto.builder()
                .id(video.getId())
                .path(video.getPath())
                .createdBy(video.getCreatedBy())
                .createdDate(video.getCreatedDate())
                .build();
    }
}
