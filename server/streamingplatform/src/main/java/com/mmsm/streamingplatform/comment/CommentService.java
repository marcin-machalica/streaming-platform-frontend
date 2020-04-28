package com.mmsm.streamingplatform.comment;

import com.mmsm.streamingplatform.comment.commentrating.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingRepository;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentRatingRepresentation;
import com.mmsm.streamingplatform.comment.CommentController.*;
import com.mmsm.streamingplatform.keycloak.service.KeycloakService;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CommentService {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    static class VideoNotFoundException extends RuntimeException {
        VideoNotFoundException(Long id) {
            super("Video not found with id: " + id);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    static class CommentNotFoundException extends RuntimeException {
        CommentNotFoundException(Long id) {
            super("Comment not found with id: " + id);
        }
    }

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final VideoRepository videoRepository;
    private final KeycloakService keycloakService;

    CommentRepresentation getCommentDtoWithReplies(Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        CommentRating commentRating = commentRatingRepository
            .findCommentRatingByCommentIdAndUserId(comment.getId(), userId)
            .orElseGet(CommentRating::new);
        CommentWithRepliesAndAuthors commentWithRepliesAndAuthors = getCommentWithRepliesAndAuthors(comment, commentRating, userId);
        return Comment.getCommentRepresentationWithReplies(commentWithRepliesAndAuthors);
    }

    CommentWithRepliesAndAuthors getCommentWithRepliesAndAuthors(Comment comment, CommentRating commentRating, String userId) {
//        if (comment == null) {
//            return null;
//        }

        Map<Comment, CommentRating> commentsAndRatings = new HashMap<>();
        comment.getDirectReplies().stream()
            .forEach(reply -> commentsAndRatings.put(
                    reply,
                    commentRatingRepository.findCommentRatingByCommentIdAndUserId(reply.getId(), userId).orElseGet(CommentRating::new)
            ));

        return new CommentWithRepliesAndAuthors(comment, keycloakService.getUserDtoById(comment.getCreatedById()),
            commentRating.toCommentRatingRepresentation(comment.getId()), getCommentsListWithRepliesAndAuthors(commentsAndRatings, userId));
    }

    List<CommentWithRepliesAndAuthors> getCommentsListWithRepliesAndAuthors(Map<Comment, CommentRating> commentsAndRatings, String userId) {
        List<CommentWithRepliesAndAuthors> nestedCommentsWithAuthors = new ArrayList<>();
        commentsAndRatings.forEach((comment, rating) -> nestedCommentsWithAuthors.add(getCommentWithRepliesAndAuthors(comment, rating, userId)));
        return nestedCommentsWithAuthors;
    }

    CommentRepresentation saveComment(CommentRepresentation dto, Long videoId) {
        Comment comment = Comment.of(dto.getMessage());
//        CommentRating commentRating = new CommentRating();
//        comment.setCommentRatings(Arrays.asList(commentRating));

        Optional<Comment> parentCommentOptional = Optional.ofNullable(dto.getParentId())
            .flatMap(commentRepository::findById);

        if (parentCommentOptional.isPresent()) {
            Comment parentComment = parentCommentOptional.get();
            parentComment.addChildrenComment(comment);
            comment = commentRepository.save(comment);
//            CommentRatingRepresentation commentRatingRepresentation = commentRating.toCommentRatingRepresentation(comment.getId());
            return CommentMapper.getCommentDto(comment, keycloakService.getUserDtoById(comment.getCreatedById()), commentRatingRepresentation);
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
        CommentRatingRepresentation commentRatingRepresentation = commentRating.toCommentRatingRepresentation(comment.getId());
        return CommentMapper.getCommentDto(comment, keycloakService.getUserDtoById(comment.getCreatedById()), commentRatingRepresentation);
    }

    CommentRepresentation updateComment(CommentRepresentation dto, String authorId, Long commentId) {
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
        CommentRatingRepresentation commentRatingRepresentation = commentRating.toCommentRatingRepresentation(comment.getId());
        return CommentMapper.getCommentDto(comment, keycloakService.getUserDtoById(comment.getCreatedById()), commentRatingRepresentation);
    }

    void deleteCommentById(Long videoId, Long commentId, String userId) {
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
