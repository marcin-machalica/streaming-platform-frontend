package com.mmsm.streamingplatform.comment.service;

import com.mmsm.streamingplatform.comment.mapper.CommentMapper;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.comment.model.CommentWithRepliesAndAuthors;
import com.mmsm.streamingplatform.keycloak.service.KeycloakService;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final KeycloakService keycloakService;

    public CommentDto getCommentDtoWithReplies(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            return null;
        }
        Comment comment = commentOptional.get();
        CommentWithRepliesAndAuthors commentWithRepliesAndAuthors = getCommentWithRepliesAndAuthors(comment);
        return CommentMapper.getCommentDtoWithReplies(commentWithRepliesAndAuthors);
    }

    public CommentWithRepliesAndAuthors getCommentWithRepliesAndAuthors(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentWithRepliesAndAuthors.builder()
                .comment(comment)
                .author(keycloakService.getUserDtoById(comment.getCreatedById()))
                .commentsAndAuthors(getCommentsWithRepliesAndAuthors(comment.getDirectReplies()))
                .build();
    }

    public List<CommentWithRepliesAndAuthors> getCommentsWithRepliesAndAuthors(List<Comment> comments) {
        List<CommentWithRepliesAndAuthors> nestedCommentsWithAuthors = new ArrayList<>();
        for (Comment comment : comments) {
            nestedCommentsWithAuthors.add(getCommentWithRepliesAndAuthors(comment));
        }
        return nestedCommentsWithAuthors;
    }

    public CommentDto saveComment(CommentDto dto, Long videoId) {
        if (dto == null || videoId == null) {
            return null;
        }
        Comment comment = new Comment();
        Optional<Comment> parentCommentOptional = Optional.ofNullable(dto.getParentId())
                .flatMap(commentRepository::findById);

        comment.setMessage(dto.getMessage());

        if (parentCommentOptional.isPresent()) {
            Comment parentComment = parentCommentOptional.get();
            parentComment.addChildrenComment(comment);
            Comment savedComment = commentRepository.save(comment);
            return CommentMapper.getCommentDto(savedComment, keycloakService.getUserDtoById(savedComment.getCreatedById()));
        }

        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isEmpty()) {
            return null;
        }
        Video video = videoOptional.get();
        List<Comment> comments = video.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
            video.setComments(comments);
        }
        comments.add(comment);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.getCommentDto(savedComment, keycloakService.getUserDtoById(savedComment.getCreatedById()));
    }

    public CommentDto updateComment(CommentDto dto, String authorId, Long commentId) {
        if (dto == null || authorId == null || commentId == null) {
            return null;
        }
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty() || !authorId.equals(commentOptional.get().getCreatedById())) {
            return null;
        }
        Comment comment = commentOptional.get();
        comment.setMessage(dto.getMessage());
        comment.setWasEdited(true);

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.getCommentDto(savedComment, keycloakService.getUserDtoById(savedComment.getCreatedById()));
    }

    public boolean deleteCommentById(Long videoId, Long commentId, String userId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (videoOptional.isEmpty() || commentOptional.isEmpty() || userId == null) {
            return false;
        }
        Video video = videoOptional.get();
        Comment comment = commentOptional.get();
        if (!userId.equals(comment.getCreatedById()) && !userId.equals(video.getCreatedById())
            && !keycloakService.isAdmin(userId)) {
            return false;
        }
        comment.setIsDeleted(true);
        commentRepository.save(comment);
        return true;
    }
}
