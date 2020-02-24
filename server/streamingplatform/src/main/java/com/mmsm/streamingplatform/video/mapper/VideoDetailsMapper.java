package com.mmsm.streamingplatform.video.mapper;

import com.mmsm.streamingplatform.comment.mapper.CommentMapper;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDetailsDto;
import com.mmsm.streamingplatform.video.videorating.mapper.VideoRatingMapper;

public class VideoDetailsMapper {

    public static VideoDetailsDto getVideoDetailsDtoFromEntity(Video entity) {
        if (entity == null) {
            return null;
        }
        return VideoDetailsDto.builder()
                .videoDto(VideoMapper.getVideoDtoFromEntity(entity))
                .videoRatingDto(VideoRatingMapper.getVideoRatingDtoFromEntity(entity))
                .directCommentDtos(CommentMapper.getCommentDtosFromEntity(entity.getComments()))
                .build();
    }
}
