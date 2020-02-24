package com.mmsm.streamingplatform.comment.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentDto {
    private Long id;
    private Long parentId;
    private String message;
    private Long upVoteCount;
    private Long downVoteCount;
    private Long favouriteVoteCount;
    private Integer repliesCount;
    private Boolean isVideoAuthorFavourite;
    private Boolean isPinned;
    private List<CommentDto> directReplies;
    // author todo
}


