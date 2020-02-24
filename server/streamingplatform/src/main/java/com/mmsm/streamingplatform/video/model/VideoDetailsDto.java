package com.mmsm.streamingplatform.video.model;

import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VideoDetailsDto {
    private VideoDto videoDto;
    private VideoRatingDto videoRatingDto;
    private List<CommentDto> directCommentDtos;
}
