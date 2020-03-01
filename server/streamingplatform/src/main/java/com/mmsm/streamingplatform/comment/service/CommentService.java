package com.mmsm.streamingplatform.comment.service;

import com.mmsm.streamingplatform.comment.mapper.CommentMapper;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.comment.model.CommentWithRepliesAndAuthors;
import com.mmsm.streamingplatform.keycloak.service.KeycloakService;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public CommentWithRepliesAndAuthors getCommentWithRepliesAndAuthors (Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentWithRepliesAndAuthors.builder()
                .comment(comment)
                .author(keycloakService.getUserDtoById(comment.getAuthorId()))
                .commentsAndAuthors(getCommentsWithRepliesAndAuthors(comment.getDirectReplies()))
                .build();
    }

    public List<CommentWithRepliesAndAuthors> getCommentsWithRepliesAndAuthors (List<Comment> comments) {
        List<CommentWithRepliesAndAuthors> nestedCommentsWithAuthors = new ArrayList<>();
        for (Comment comment : comments) {
            nestedCommentsWithAuthors.add(getCommentWithRepliesAndAuthors(comment));
        }
        return nestedCommentsWithAuthors;
    }

    public CommentDto saveComment(CommentDto dto, String authorId, Long videoId) {
        if (dto == null || videoId == null) {
            return null;
        }
        Comment comment = new Comment();
        Optional<Comment> parentCommentOptional = Optional.ofNullable(dto.getParentId())
                .flatMap(commentRepository::findById);

        comment.setMessage(dto.getMessage());
        comment.setAuthorId(authorId);

        if (parentCommentOptional.isPresent()) {
            Comment parentComment = parentCommentOptional.get();
            parentComment.addChildrenComment(comment);
            Comment savedComment = commentRepository.save(comment);
            return CommentMapper.getCommentDto(savedComment,
                    keycloakService.getUserDtoById(savedComment.getAuthorId()));
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
        return CommentMapper.getCommentDto(savedComment,
                keycloakService.getUserDtoById(savedComment.getAuthorId()));
    }
}
