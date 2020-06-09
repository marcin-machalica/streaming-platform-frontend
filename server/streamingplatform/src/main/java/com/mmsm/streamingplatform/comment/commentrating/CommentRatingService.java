package com.mmsm.streamingplatform.comment.commentrating;

import com.mmsm.streamingplatform.comment.Comment;
import com.mmsm.streamingplatform.comment.CommentController.CommentNotFoundException;
import com.mmsm.streamingplatform.comment.CommentRepository;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CanOnlyBePerformedByVideoAuthorException;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentRatingRepresentation;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentFavouriteDto;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.NotDirectVideoCommentException;
import com.mmsm.streamingplatform.utils.CommonExceptionsUtils.CannotBePerformedByAuthorException;
import com.mmsm.streamingplatform.video.Video;
import com.mmsm.streamingplatform.video.VideoController.VideoNotFoundException;
import com.mmsm.streamingplatform.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class CommentRatingService {

    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;

    @Transactional
    CommentRatingRepresentation upVoteComment(Long commentId, String userId) {
        Comment comment = getCommentIfNotAuthor(commentId, userId);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::of);
        commentRating = commentRating.getId() != null ? commentRating : comment.addCommentRating(commentRating);

        comment = comment.upVote(commentRating);
        commentRating = commentRating.upVote();

        commentRepository.save(comment);
        commentRating = commentRatingRepository.save(commentRating);
        return commentRating.toRepresentation(commentId);
    }

    @Transactional
    CommentRatingRepresentation downVoteComment(Long commentId, String userId) {
        Comment comment = getCommentIfNotAuthor(commentId, userId);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::of);
        commentRating = commentRating.getId() != null ? commentRating : comment.addCommentRating(commentRating);

        comment = comment.downVote(commentRating);
        commentRating = commentRating.downVote();

        commentRepository.save(comment);
        commentRating = commentRatingRepository.save(commentRating);
        return commentRating.toRepresentation(commentId);
    }

    @Transactional
    CommentFavouriteDto favouriteComment(Long videoId, Long commentId, String userId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::of);
        commentRating = commentRating.getId() != null ? commentRating : comment.addCommentRating(commentRating);

        comment = comment.favourite(commentRating, video.getCreatedById().equals(userId));
        commentRating = commentRating.favourite();

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
