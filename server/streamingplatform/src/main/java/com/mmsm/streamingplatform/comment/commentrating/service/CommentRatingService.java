package com.mmsm.streamingplatform.comment.commentrating.service;

import com.mmsm.streamingplatform.comment.commentrating.mapper.CommentRatingMapper;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.service.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentRatingService {

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;

    public CommentRatingDto upVoteComment(Long commentId, String userId) {
        Optional<CommentRating> commentRatingOptional = commentRatingRepository.findCommentRatingByCommentIdAndUserId(commentId, userId);

        if (commentRatingOptional.isPresent()) {
            Comment comment = commentRepository.getOne(commentId);
            CommentRating commentRating = commentRatingOptional.get();

            Boolean isUpVote = commentRating.getIsUpVote();
            commentRating.setIsUpVote(!isUpVote);
            comment.setUpVoteCount(comment.getUpVoteCount() + (isUpVote ? -1 : 1));

            Boolean isDownVote = commentRating.getIsDownVote();
            if (isDownVote) {
                commentRating.setIsDownVote(false);
                comment.setDownVoteCount(comment.getDownVoteCount() - 1);
            }

            commentRating = commentRatingRepository.save(commentRating);
            return CommentRatingMapper.getCommentRatingDto(commentRating, commentId);
        }

        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            return null;
        }
        Comment comment = commentOptional.get();
        List<CommentRating> commentRatings = comment.getCommentRatings();
        CommentRating commentRating = new CommentRating();
        commentRating.setIsUpVote(true);
        comment.setUpVoteCount(comment.getUpVoteCount() + 1);
        commentRatings.add(commentRating);
        commentRating = commentRatingRepository.save(commentRating);
        return CommentRatingMapper.getCommentRatingDto(commentRating, commentId);
    }
}
