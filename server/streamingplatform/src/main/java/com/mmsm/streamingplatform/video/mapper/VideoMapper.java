package com.mmsm.streamingplatform.video.mapper;

import com.mmsm.streamingplatform.comment.mapper.CommentMapper;
import com.mmsm.streamingplatform.comment.model.CommentWithRepliesAndAuthors;
import com.mmsm.streamingplatform.keycloak.model.UserDto;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDetailsDto;
import com.mmsm.streamingplatform.video.model.VideoDto;
import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;

import java.util.List;

public class VideoMapper {

    public static VideoDto getVideoDtoFromEntity(Video entity, UserDto author) {
        if (entity == null) {
            return null;
        }
        return VideoDto.builder()
                .id(entity.getId())
                .author(author)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdDate(entity.getCreatedDate())
                .build();
    }

    public static VideoDetailsDto getVideoDetailsDtoFromEntity(Video video, UserDto videoAuthor, VideoRatingDto currentUserVideoRating,
                                                               List<CommentWithRepliesAndAuthors> commentWithRepliesAndAuthors) {
        if (video == null) {
            return null;
        }

        return VideoDetailsDto.builder()
                .id(video.getId())
                .author(videoAuthor)
                .title(video.getTitle())
                .description(video.getDescription())
                .upVoteCount(video.getUpVoteCount())
                .downVoteCount(video.getDownVoteCount())
                .viewCount(video.getViewCount())
                .shareCount(video.getShareCount())
                .createdDate(video.getCreatedDate())
                .currentUserVideoRating(currentUserVideoRating)
                .directCommentDtos(CommentMapper.getCommentDtosWithReplies(commentWithRepliesAndAuthors))
                .build();
    }
}
