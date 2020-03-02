package com.mmsm.streamingplatform.video.mapper;

import com.mmsm.streamingplatform.keycloak.model.UserDto;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDto;

public class VideoMapper {

    public static VideoDto getVideoDtoFromEntity(Video entity, UserDto author) {
        if (entity == null) {
            return null;
        }
        return VideoDto.builder()
                .id(entity.getId())
                .author(author)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdDate(entity.getCreatedDate())
                .build();
    }
}
