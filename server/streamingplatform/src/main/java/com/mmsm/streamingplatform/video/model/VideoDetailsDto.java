package com.mmsm.streamingplatform.video.model;

import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.keycloak.model.UserDto;
import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class VideoDetailsDto {
    private Long id;
    private UserDto author;
    private String title;
    private String description;
    private Long upVoteCount;
    private Long downVoteCount;
    private Long viewCount;
    private Long shareCount;
    private LocalDateTime createdDate;
    private VideoRatingDto currentUserVideoRating;
    private List<CommentDto> directCommentDtos;
}
