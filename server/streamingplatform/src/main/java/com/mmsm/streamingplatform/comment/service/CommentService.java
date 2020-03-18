package com.mmsm.streamingplatform.comment.service;

import com.mmsm.streamingplatform.comment.commentrating.mapper.CommentRatingMapper;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.commentrating.service.CommentRatingRepository;
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
    private final CommentRatingRepository commentRatingRepository;
    private final VideoRepository videoRepository;
    private final KeycloakService keycloakService;

    public CommentDto getCommentDtoWithReplies(Long id, String userId) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            return null;
        }
        Comment comment = commentOptional.get();
        CommentRating commentRating = commentRatingRepository
                .findCommentRatingByCommentIdAndUserId(comment.getId(), userId).orElseGet(CommentRating::new);
        CommentWithRepliesAndAuthors commentWithRepliesAndAuthors = getCommentWithRepliesAndAuthors(comment, commentRating, userId);
        return CommentMapper.getCommentDtoWithReplies(commentWithRepliesAndAuthors);
    }

    public CommentWithRepliesAndAuthors getCommentWithRepliesAndAuthors(Comment comment, CommentRating commentRating, String userId) {
        if (comment == null) {
            return null;
        }

        Map<Comment, CommentRating> commentsAndRatings = new HashMap<>();
        comment.getDirectReplies().stream()
                .forEach(reply -> commentsAndRatings.put(
                        reply,
                        commentRatingRepository.findCommentRatingByCommentIdAndUserId(reply.getId(), userId).orElseGet(CommentRating::new)
                ));

        return CommentWithRepliesAndAuthors.builder()
                .comment(comment)
                .author(keycloakService.getUserDtoById(comment.getCreatedById()))
                .commentRatingDto(CommentRatingMapper.getCommentRatingDto(commentRating, comment.getId()))
                .commentsAndAuthors(getCommentsWithRepliesAndAuthors(commentsAndRatings, userId))
                .build();
    }

    public List<CommentWithRepliesAndAuthors> getCommentsWithRepliesAndAuthors(Map<Comment, CommentRating> commentsAndRatings, String userId) {
        List<CommentWithRepliesAndAuthors> nestedCommentsWithAuthors = new ArrayList<>();
        commentsAndRatings.forEach((comment, rating) -> nestedCommentsWithAuthors.add(getCommentWithRepliesAndAuthors(comment, rating, userId)));
        return nestedCommentsWithAuthors;
    }

    public CommentDto saveComment(CommentDto dto, Long videoId) {
        if (dto == null || videoId == null) {
            return null;
        }
        Comment comment = new Comment();
        CommentRating commentRating = new CommentRating();
        comment.setCommentRatings(Arrays.asList(commentRating));

        Optional<Comment> parentCommentOptional = Optional.ofNullable(dto.getParentId())
                .flatMap(commentRepository::findById);
        comment.setMessage(dto.getMessage());

        if (parentCommentOptional.isPresent()) {
            Comment parentComment = parentCommentOptional.get();
            parentComment.addChildrenComment(comment);
            comment = commentRepository.save(comment);
            CommentRatingDto commentRatingDto = CommentRatingMapper.getCommentRatingDto(commentRating, comment.getId());
            return CommentMapper.getCommentDto(comment, keycloakService.getUserDtoById(comment.getCreatedById()), commentRatingDto);
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
        comment = commentRepository.save(comment);
        CommentRatingDto commentRatingDto = CommentRatingMapper.getCommentRatingDto(commentRating, comment.getId());
        return CommentMapper.getCommentDto(comment, keycloakService.getUserDtoById(comment.getCreatedById()), commentRatingDto);
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

        comment = commentRepository.save(comment);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(comment.getId(), authorId).orElse(null);
        CommentRatingDto commentRatingDto = CommentRatingMapper.getCommentRatingDto(commentRating, comment.getId());
        return CommentMapper.getCommentDto(comment, keycloakService.getUserDtoById(comment.getCreatedById()), commentRatingDto);
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
