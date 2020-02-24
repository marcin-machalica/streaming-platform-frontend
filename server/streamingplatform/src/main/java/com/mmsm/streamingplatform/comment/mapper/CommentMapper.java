package com.mmsm.streamingplatform.comment.mapper;

import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.model.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto getCommentDtoFromEntity(Comment entity) {
        if (entity == null) {
            return null;
        }
        return CommentDto.builder()
                .id(entity.getId())
                .parentId(entity.getParentComment() != null ? entity.getParentComment().getId() : null)
                .message(entity.getMessage())
                .upVoteCount(entity.getUpVoteCount())
                .downVoteCount(entity.getDownVoteCount())
                .favouriteVoteCount(entity.getFavouriteVoteCount())
                .repliesCount(entity.getRepliesCount())
                .isVideoAuthorFavourite(entity.getIsVideoAuthorFavourite())
                .isPinned(entity.getIsPinned())
                // author todo
                .build();
    }

    public static List<CommentDto> getCommentDtosFromEntity(List<Comment> entities) {
        if (entities == null) {
            return null;
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        for(Comment entity : entities) {
            commentDtos.add(getCommentDtoFromEntity(entity));
        }
        return commentDtos;
    }

    public static CommentDto getCommentDtosWithRepliesFromEntity(Comment entity) {
        if (entity == null) {
            return null;
        }
        return CommentDto.builder()
                .id(entity.getId())
                .parentId(entity.getParentComment() != null ? entity.getParentComment().getId() : null)
                .message(entity.getMessage())
                .upVoteCount(entity.getUpVoteCount())
                .downVoteCount(entity.getDownVoteCount())
                .favouriteVoteCount(entity.getFavouriteVoteCount())
                .repliesCount(entity.getRepliesCount())
                .isVideoAuthorFavourite(entity.getIsVideoAuthorFavourite())
                .isPinned(entity.getIsPinned())
                .directReplies(CommentMapper.getCommentDtosWithRepliesFromEntity(entity.getDirectReplies()))
                // author todo
                .build();
    }

    private static List<CommentDto> getCommentDtosWithRepliesFromEntity(List<Comment> entities) {
        if (entities == null) {
            return null;
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        for(Comment entity : entities) {
            commentDtos.add(getCommentDtosWithRepliesFromEntity(entity));
        }
        return commentDtos;
    }
}
