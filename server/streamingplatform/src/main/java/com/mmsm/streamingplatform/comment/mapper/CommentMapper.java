package com.mmsm.streamingplatform.comment.mapper;

import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.comment.model.CommentWithRepliesAndAuthors;
import com.mmsm.streamingplatform.keycloak.model.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentMapper {

    public static CommentDto getCommentDto(Comment entity, UserDto author) {
        if (entity == null) {
            return null;
        }
        return CommentDto.builder()
                .id(entity.getId())
                .parentId(entity.getParentComment() != null ? entity.getParentComment().getId() : null)
                .author(author)
                .message(entity.getMessage())
                .upVoteCount(entity.getUpVoteCount())
                .downVoteCount(entity.getDownVoteCount())
                .favouriteVoteCount(entity.getFavouriteVoteCount())
                .directRepliesCount(entity.getDirectRepliesCount())
                .allRepliesCount(entity.getAllRepliesCount())
                .isVideoAuthorFavourite(entity.getIsVideoAuthorFavourite())
                .isPinned(entity.getIsPinned())
                .dateCreated(entity.getCreatedDate())
                .build();
    }

    public static List<CommentDto> getCommentDtos(Map<Comment, UserDto> commentsAndAuthors) {
        List<CommentDto> commentDtos = new ArrayList<>();
        commentsAndAuthors.forEach((comment, author) ->
                commentDtos.add(getCommentDto(comment, author))
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
                .message(comment.getMessage())
                .upVoteCount(comment.getUpVoteCount())
                .downVoteCount(comment.getDownVoteCount())
                .favouriteVoteCount(comment.getFavouriteVoteCount())
                .directRepliesCount(comment.getDirectRepliesCount())
                .allRepliesCount(comment.getAllRepliesCount())
                .isVideoAuthorFavourite(comment.getIsVideoAuthorFavourite())
                .isPinned(comment.getIsPinned())
                .dateCreated(comment.getCreatedDate())
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
