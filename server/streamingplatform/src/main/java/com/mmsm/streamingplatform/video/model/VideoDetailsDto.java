package com.mmsm.streamingplatform.video.model;

import com.mmsm.streamingplatform.comment.CommentController.*;
import com.mmsm.streamingplatform.keycloak.KeycloakController.UserDto;
import com.mmsm.streamingplatform.video.videorating.VideoRatingController.*;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
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
    private Instant createdDate;
    private VideoRatingRepresentation currentUserVideoRating;
    private List<CommentRepresentation> directCommentDtos;
}
