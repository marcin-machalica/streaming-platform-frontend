package com.mmsm.streamingplatform.comment.service;

import com.mmsm.streamingplatform.comment.mapper.CommentMapper;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;

    public CommentDto getCommentWithReplyDtos(Long id) {
        return commentRepository.findById(id)
                .map(CommentMapper::getCommentDtosWithRepliesFromEntity)
                .orElse(null);
    }

    public CommentDto saveComment(CommentDto dto, Long videoId) {
        if (dto == null || videoId == null) {
            return null;
        }
        Comment comment = new Comment();
        Optional<Comment> parentCommentOptional = Optional.ofNullable(dto.getParentId())
                .flatMap(commentRepository::findById);

        if (parentCommentOptional.isPresent()) {
            Comment parentComment = parentCommentOptional.get();
            comment.setMessage(dto.getMessage());
            parentComment.addChildrenComment(comment);
            Comment savedComment = commentRepository.save(comment);
            return CommentMapper.getCommentDtoFromEntity(savedComment);
        }

        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isEmpty()) {
            return null;
        }
        Video video = videoOptional.get();
        comment.setMessage(dto.getMessage());
        List<Comment> comments = video.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
            video.setComments(comments);
        }
        comments.add(comment);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.getCommentDtoFromEntity(savedComment);
    }
}
