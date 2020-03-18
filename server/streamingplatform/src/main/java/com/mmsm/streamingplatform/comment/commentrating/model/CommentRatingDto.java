package com.mmsm.streamingplatform.comment.commentrating.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentRatingDto {
    private Long commentId;
    private Boolean isUpVote;
    private Boolean isDownVote;
    private Boolean isFavourite;
}
