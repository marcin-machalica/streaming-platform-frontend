package com.mmsm.streamingplatform.comment;

import com.mmsm.streamingplatform.channel.Channel;
import com.mmsm.streamingplatform.channel.ChannelController.ChannelNotFoundByUserIdException;
import com.mmsm.streamingplatform.channel.ChannelRepository;
import com.mmsm.streamingplatform.comment.commentrating.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingRepository;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentRatingRepresentation;
import com.mmsm.streamingplatform.comment.CommentController.*;
import com.mmsm.streamingplatform.security.keycloak.KeycloakService;
import com.mmsm.streamingplatform.utils.CommonExceptionsUtils.CanOnlyBePerformedByAuthorException;
import com.mmsm.streamingplatform.utils.CommonExceptionsUtils.NotSufficientPermissionsException;
import com.mmsm.streamingplatform.video.Video;
import com.mmsm.streamingplatform.video.VideoController.VideoNotFoundException;
import com.mmsm.streamingplatform.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final VideoRepository videoRepository;
    private final ChannelRepository channelRepository;
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
        Map<Comment, CommentRating> commentsAndRatings = new HashMap<>();
        comment.getDirectReplies().stream()
            .forEach(reply -> commentsAndRatings.put(
                    reply,
                    commentRatingRepository.findCommentRatingByCommentIdAndUserId(reply.getId(), userId).orElseGet(CommentRating::of)
            ));

        return new CommentWithRepliesAndAuthors(comment, keycloakService.getUserDtoById(comment.getCreatedById()),
            commentRating.toRepresentation(comment.getId()), getCommentsListWithRepliesAndAuthors(commentsAndRatings, userId));
    }

    public List<CommentWithRepliesAndAuthors> getCommentsListWithRepliesAndAuthors(Map<Comment, CommentRating> commentsAndRatings, String userId) {
        List<CommentWithRepliesAndAuthors> nestedCommentsWithAuthors = new ArrayList<>();
        commentsAndRatings.forEach((comment, rating) -> nestedCommentsWithAuthors.add(getCommentWithRepliesAndAuthors(comment, rating, userId)));
        return nestedCommentsWithAuthors;
    }

    CommentRepresentation saveComment(SaveComment saveComment, Long videoId, String userId) {
        Channel channel = channelRepository.findByAuditorCreatedById(userId).orElseThrow(() -> new ChannelNotFoundByUserIdException(userId));

        Comment comment = Comment.of(saveComment.getMessage(), channel);
        Optional<Comment> parentCommentOptional = Optional.ofNullable(saveComment.getParentId())
            .flatMap(commentRepository::findById);

        Comment finalComment = comment;
        parentCommentOptional.ifPresentOrElse(parentComment -> {
            parentComment.addChildrenComment(finalComment);
        }, () -> {
            Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
            video.addComment(finalComment);
        });

        comment = commentRepository.save(comment);
        CommentRatingRepresentation commentRatingRepresentation = CommentRating.of().toRepresentation(comment.getId());
        return comment.toRepresentation(commentRatingRepresentation);
    }

    CommentRepresentation updateComment(CommentUpdate commentUpdate, Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!userId.equals(comment.getCreatedById())) {
            throw new CanOnlyBePerformedByAuthorException();
        }

        comment = comment.updateComment(commentUpdate);
        comment = commentRepository.save(comment);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(comment.getId(), userId).orElseGet(CommentRating::of);
        CommentRatingRepresentation commentRatingRepresentation = commentRating.toRepresentation(comment.getId());
        return comment.toRepresentation(commentRatingRepresentation);
    }

    void deleteCommentById(Long videoId, Long commentId, String userId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!userId.equals(comment.getCreatedById()) && !userId.equals(video.getCreatedById()) && !keycloakService.isAdmin(userId)) {
            throw new NotSufficientPermissionsException();
        }

        comment = comment.setDeleted();
        commentRepository.save(comment);
    }
}
