package com.mmsm.streamingplatform.comment.commentrating.mapper;

import com.mmsm.streamingplatform.comment.commentrating.model.CommentFavouriteDto;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.model.Comment;

public class CommentRatingMapper {

    public static CommentRatingDto toCommentRatingDto(CommentRating commentRating, Long commentId) {
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

    public static CommentFavouriteDto toCommentFavouriteDto(Comment comment, CommentRating commentRating) {
        return CommentFavouriteDto.builder()
                .favouriteCount(comment.getFavouriteCount())
                .isFavourite(commentRating.getIsFavourite())
                .isVideoAuthorFavourite(comment.getIsVideoAuthorFavourite())
                .build();
    }
}
