package com.mmsm.streamingplatform.comment.commentrating.service;

import com.mmsm.streamingplatform.comment.commentrating.mapper.CommentRatingMapper;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.service.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentRatingService {

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;

    public CommentRatingDto upVoteComment(Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
        Comment comment = getCommentAndThrowIfNotFoundOrUserIsAuthor(commentId, userId);
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

        commentRating = commentRatingRepository.save(commentRating);
        return CommentRatingMapper.getCommentRatingDto(commentRating, commentId);
    }

    public CommentRatingDto downVoteComment(Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
        Comment comment = getCommentAndThrowIfNotFoundOrUserIsAuthor(commentId, userId);
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

        commentRating = commentRatingRepository.save(commentRating);
        return CommentRatingMapper.getCommentRatingDto(commentRating, commentId);
    }

    public CommentRatingDto favouriteComment(Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
        Comment comment = getCommentAndThrowIfNotFoundOrUserIsAuthor(commentId, userId);
        CommentRating commentRating = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId).orElseGet(CommentRating::new);
        Boolean wasFavourite = commentRating.getIsFavourite();
        commentRating.setIsFavourite(!wasFavourite);
        comment.setFavouriteCount(comment.getFavouriteCount() + (wasFavourite ? -1 : 1));

        if (commentRating.getId() == null) {
            comment.getCommentRatings().add(commentRating);
        }

        commentRating = commentRatingRepository.save(commentRating);
        return CommentRatingMapper.getCommentRatingDto(commentRating, commentId);
    }

    private Comment getCommentAndThrowIfNotFoundOrUserIsAuthor(Long commentId, String userId) throws NotFoundException, NotAuthorizedException {
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
