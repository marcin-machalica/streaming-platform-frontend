package com.mmsm.streamingplatform.comment.mapper;

import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.comment.model.CommentWithRepliesAndAuthors;
import com.mmsm.streamingplatform.keycloak.model.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentMapper {

    public static CommentDto getCommentDto(Comment comment, UserDto author, CommentRatingDto currentUserCommentRating) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .author(author)
                .message(comment.getIsDeleted() ? null : comment.getMessage())
                .upVoteCount(comment.getUpVoteCount())
                .downVoteCount(comment.getDownVoteCount())
                .favouriteCount(comment.getFavouriteCount())
                .directRepliesCount(comment.getDirectRepliesCount())
                .allRepliesCount(comment.getAllRepliesCount())
                .isVideoAuthorFavourite(comment.getIsVideoAuthorFavourite())
                .isPinned(comment.getIsPinned())
                .wasEdited(comment.getWasEdited())
                .isDeleted(comment.getIsDeleted())
                .dateCreated(comment.getCreatedDate())
                .currentUserCommentRating(currentUserCommentRating)
                .build();
    }

    public static List<CommentDto> getCommentDtos(Map<Comment, UserDto> commentsAndAuthors, CommentRatingDto currentUserCommentRating) {
        List<CommentDto> commentDtos = new ArrayList<>();
        commentsAndAuthors.forEach((comment, author) ->
                commentDtos.add(getCommentDto(comment, author, currentUserCommentRating))
        );
        return commentDtos;
    }

    public static CommentDto getCommentDtoWithReplies(CommentWithRepliesAndAuthors commentWithRepliesAndAuthors) {
        if (commentWithRepliesAndAuthors == null || commentWithRepliesAndAuthors.getComment() == null) {
            return null;
        }
        Comment comment = commentWithRepliesAndAuthors.getComment();

        return CommentDto.builder()
                .id(comment.getId())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .author(commentWithRepliesAndAuthors.getAuthor())
                .message(comment.getIsDeleted() ? null : comment.getMessage())
                .upVoteCount(comment.getUpVoteCount())
                .downVoteCount(comment.getDownVoteCount())
                .favouriteCount(comment.getFavouriteCount())
                .directRepliesCount(comment.getDirectRepliesCount())
                .allRepliesCount(comment.getAllRepliesCount())
                .isVideoAuthorFavourite(comment.getIsVideoAuthorFavourite())
                .isPinned(comment.getIsPinned())
                .wasEdited(comment.getWasEdited())
                .isDeleted(comment.getIsDeleted())
                .dateCreated(comment.getCreatedDate())
                .currentUserCommentRating(commentWithRepliesAndAuthors.getCommentRatingDto())
                .directReplies(CommentMapper.getCommentDtosWithReplies(commentWithRepliesAndAuthors.getCommentsAndAuthors()))
                .build();
    }

    public static List<CommentDto> getCommentDtosWithReplies(List<CommentWithRepliesAndAuthors> commentsWithRepliesAndAuthors) {
        if (commentsWithRepliesAndAuthors == null) {
            return null;
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        for(CommentWithRepliesAndAuthors entity : commentsWithRepliesAndAuthors) {
            commentDtos.add(getCommentDtoWithReplies(entity));
        }
        return commentDtos;
    }
}
