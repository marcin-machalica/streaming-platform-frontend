package com.mmsm.streamingplatform.comment.commentrating.mapper;

import com.mmsm.streamingplatform.comment.commentrating.model.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;

public class CommentRatingMapper {

    public static CommentRatingDto getCommentRatingDto(CommentRating commentRating, Long commentId) {
        if (commentRating == null || commentId == null) {
            return null;
        }
        return CommentRatingDto.builder()
                .commentId(commentId)
                .isUpVote(commentRating.getIsUpVote())
                .isDownVote(commentRating.getIsDownVote())
                .isFavourite(commentRating.getIsFavourite())
                .build();
    }
}
