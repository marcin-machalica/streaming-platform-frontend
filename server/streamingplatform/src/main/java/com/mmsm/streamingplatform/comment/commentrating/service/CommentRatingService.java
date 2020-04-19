package com.mmsm.streamingplatform.comment.commentrating.service;

import com.mmsm.streamingplatform.comment.commentrating.mapper.CommentRatingMapper;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentFavouriteDto;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.service.CommentRepository;
import com.mmsm.streamingplatform.keycloak.service.KeycloakService;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentRatingService {

    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final KeycloakService keycloakService;

    @Transactional
    public CommentRatingDto upVoteComment(Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
        Comment comment = getCommentOrThrowIfNotFoundOrUserIsAuthor(commentId, userId);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::new);
        Boolean wasUpVote = commentRating.getIsUpVote();
        commentRating.setIsUpVote(!wasUpVote);
        comment.setUpVoteCount(comment.getUpVoteCount() + (wasUpVote ? -1 : 1));

        if (commentRating.getId() != null) {
            Boolean wasDownVote = commentRating.getIsDownVote();
            if (wasDownVote) {
                commentRating.setIsDownVote(false);
                comment.setDownVoteCount(comment.getDownVoteCount() - 1);
            }
        } else {
            comment.getCommentRatings().add(commentRating);
        }

        commentRepository.save(comment);
        commentRating = commentRatingRepository.save(commentRating);
        return CommentRatingMapper.toCommentRatingDto(commentRating, commentId);
    }

    @Transactional
    public CommentRatingDto downVoteComment(Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
        Comment comment = getCommentOrThrowIfNotFoundOrUserIsAuthor(commentId, userId);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::new);
        Boolean wasDownVote = commentRating.getIsDownVote();
        commentRating.setIsDownVote(!wasDownVote);
        comment.setDownVoteCount(comment.getDownVoteCount() + (wasDownVote ? -1 : 1));

        if (commentRating.getId() != null) {
            Boolean wasUpVote = commentRating.getIsUpVote();
            if (wasUpVote) {
                commentRating.setIsUpVote(false);
                comment.setUpVoteCount(comment.getUpVoteCount() - 1);
            }
        } else {
            comment.getCommentRatings().add(commentRating);
        }

        commentRepository.save(comment);
        commentRating = commentRatingRepository.save(commentRating);
        return CommentRatingMapper.toCommentRatingDto(commentRating, commentId);
    }

    @Transactional
    public CommentFavouriteDto favouriteComment(Long videoId, Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
        Video video = videoRepository.findById(videoId).orElseThrow(NotFoundException::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NotFoundException::new);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::new);

        Boolean wasFavourite = commentRating.getIsFavourite();
        commentRating.setIsFavourite(!wasFavourite);
        comment.setFavouriteCount(comment.getFavouriteCount() + (wasFavourite ? -1 : 1));

        if (video.getCreatedById().equals(userId)) {
            comment.setIsVideoAuthorFavourite(!comment.getIsVideoAuthorFavourite());
        }

        if (commentRating.getId() == null) {
            comment.getCommentRatings().add(commentRating);
        }

        commentRating = commentRatingRepository.save(commentRating);
        comment = commentRepository.save(comment);
        return CommentRatingMapper.toCommentFavouriteDto(comment, commentRating);
    }

    @Transactional
    public void pinComment(Long videoId, Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
        Video video = videoRepository.findById(videoId).orElseThrow(NotFoundException::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NotFoundException::new);

        if (!video.getCreatedById().equals(userId)) {
            throw new NotAuthorizedException("");
        }

        video.getComments().stream()
            .filter(c -> c.getId().equals(commentId))
            .findAny()
            .orElseThrow(() -> new BadRequestException(""));

        comment.setIsPinned(!comment.getIsPinned());
        commentRepository.save(comment);
    }

    @Transactional
    Comment getCommentOrThrowIfNotFoundOrUserIsAuthor(Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            throw new NotFoundException();
        }
        Comment comment = commentOptional.get();
        if (comment.getCreatedById().equals(userId)) {
            throw new NotAuthorizedException("");
        }
        return comment;
    }
}
