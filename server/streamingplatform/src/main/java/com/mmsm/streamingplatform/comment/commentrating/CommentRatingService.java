package com.mmsm.streamingplatform.comment.commentrating;

import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.service.CommentRepository;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentRatingRepresentation;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentFavouriteDto;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;


@Service
@Transactional
@RequiredArgsConstructor
public class CommentRatingService {

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

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    static class CannotBePerformedByAuthorException extends RuntimeException {
        CannotBePerformedByAuthorException() {
            super("This action cannot be performed by the author");
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    static class CanOnlyBePerformedByVideoAuthorException extends RuntimeException {
        CanOnlyBePerformedByVideoAuthorException() {
            super("This action can only be performed by the author");
        }
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    static class NotDirectVideoCommentException extends RuntimeException {
        NotDirectVideoCommentException() {
            super("This action can only be performed on a direct video comment");
        }
    }

    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;

    @Transactional
    CommentRatingRepresentation upVoteComment(Long commentId, String userId) {
        Comment comment = getCommentIfNotAuthor(commentId, userId);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::of);

        commentRating = commentRating.upVote();

        Boolean wasUpVote = commentRating.getIsUpVote();
        comment.setUpVoteCount(comment.getUpVoteCount() + (wasUpVote ? -1 : 1));
        if (commentRating.getIsDownVote()) {
            comment.setDownVoteCount(comment.getDownVoteCount() - 1);
        } else {
            comment.getCommentRatings().add(commentRating);
        }

        commentRepository.save(comment);
        commentRating = commentRatingRepository.save(commentRating);
        return commentRating.toCommentRatingRepresentation(commentId);
    }

    @Transactional
    CommentRatingRepresentation downVoteComment(Long commentId, String userId) {
        Comment comment = getCommentIfNotAuthor(commentId, userId);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::of);

        commentRating = commentRating.downVote();

        Boolean wasDownVote = commentRating.getIsDownVote();
        comment.setDownVoteCount(comment.getDownVoteCount() + (wasDownVote ? -1 : 1));
        if (commentRating.getIsUpVote()) {
            comment.setUpVoteCount(comment.getUpVoteCount() - 1);
        } else {
            comment.getCommentRatings().add(commentRating);
        }

        commentRepository.save(comment);
        commentRating = commentRatingRepository.save(commentRating);
        return commentRating.toCommentRatingRepresentation(commentId);
    }

    @Transactional
    CommentFavouriteDto favouriteComment(Long videoId, Long commentId, String userId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::of);

        commentRating = commentRating.favourite();

        Boolean wasFavourite = commentRating.getIsFavourite();
        comment.setFavouriteCount(comment.getFavouriteCount() + (wasFavourite ? -1 : 1));

        if (video.getCreatedById().equals(userId)) {
            comment.setIsVideoAuthorFavourite(!comment.getIsVideoAuthorFavourite());
        }

        if (commentRating.getId() == null) {
            comment.getCommentRatings().add(commentRating);
        }

        commentRating = commentRatingRepository.save(commentRating);
        comment = commentRepository.save(comment);
        return commentRating.toCommentFavouriteDto(comment.getFavouriteCount(), comment.getIsVideoAuthorFavourite());
    }

    @Transactional
    void pinComment(Long videoId, Long commentId, String userId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!video.getCreatedById().equals(userId)) {
            throw new CanOnlyBePerformedByVideoAuthorException();
        }

        video.getComments().stream()
            .filter(c -> c.getId().equals(commentId))
            .findAny()
            .orElseThrow(NotDirectVideoCommentException::new);

        comment.setIsPinned(!comment.getIsPinned());
        commentRepository.save(comment);
    }

    @Transactional
    Comment getCommentIfNotAuthor(Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        if (comment.getCreatedById().equals(userId)) {
            throw new CannotBePerformedByAuthorException();
        }
        return comment;
    }
}
