package com.mmsm.streamingplatform.video.mapper;

import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDto;

public class VideoMapper {

    public static VideoDto getVideoDtoFromEntity(Video entity) {
        if (entity == null) {
            return null;
        }

        return VideoDto.builder()
                .id(entity.getId())
                .filename(entity.getFilename())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdBy(entity.getCreatedBy())
                .createdDate(entity.getCreatedDate())
                .build();
    }
}
