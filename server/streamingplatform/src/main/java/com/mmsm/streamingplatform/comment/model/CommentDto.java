package com.mmsm.streamingplatform.comment.model;

import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.keycloak.model.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
    private Long favouriteVoteCount;
    private Integer directRepliesCount;
    private Integer allRepliesCount;
    private Boolean isVideoAuthorFavourite;
    private Boolean isPinned;
    private Boolean wasEdited;
    private Boolean isDeleted;
    private LocalDateTime dateCreated;
    private CommentRatingDto currentUserCommentRating;
    private List<CommentDto> directReplies;
}


