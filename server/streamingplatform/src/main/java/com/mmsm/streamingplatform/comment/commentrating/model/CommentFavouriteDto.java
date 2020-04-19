package com.mmsm.streamingplatform.comment.commentrating.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentFavouriteDto {
    private Long favouriteCount;
    private Boolean isFavourite;
    private Boolean isVideoAuthorFavourite;
}
