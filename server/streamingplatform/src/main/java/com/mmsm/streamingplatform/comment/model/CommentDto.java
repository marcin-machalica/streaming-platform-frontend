package com.mmsm.streamingplatform.comment.model;

import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentRatingRepresentation;
import com.mmsm.streamingplatform.keycloak.model.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class CommentDto {
    private Long id;
    private Long parentId;
    private UserDto author;
    private String message;
    private Long upVoteCount;
    private Long downVoteCount;
    private Long favouriteCount;
    private Integer directRepliesCount;
    private Integer allRepliesCount;
    private Boolean isVideoAuthorFavourite;
    private Boolean isPinned;
    private Boolean wasEdited;
    private Boolean isDeleted;
    private Instant dateCreated;
    private CommentRatingRepresentation currentUserCommentRating;
    private List<CommentDto> directReplies;
}


