package com.mmsm.streamingplatform.comment.mapper;

import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.model.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto getCommentDtoFromEntity(Comment entity, boolean withReplies) {
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
                .directRepliesCount(entity.getDirectRepliesCount())
                .allRepliesCount(entity.getAllRepliesCount())
                .isVideoAuthorFavourite(entity.getIsVideoAuthorFavourite())
                .isPinned(entity.getIsPinned())
                .dateCreated(entity.getCreatedDate())
                .directReplies(withReplies ? CommentMapper.getCommentDtosFromEntity(entity.getDirectReplies(), withReplies) : null)
                // author todo
                .build();
    }

    public static List<CommentDto> getCommentDtosFromEntity(List<Comment> entities, boolean withReplies) {
        if (entities == null) {
            return null;
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        for(Comment entity : entities) {
            commentDtos.add(getCommentDtoFromEntity(entity, withReplies));
        }
        return commentDtos;
    }
}
