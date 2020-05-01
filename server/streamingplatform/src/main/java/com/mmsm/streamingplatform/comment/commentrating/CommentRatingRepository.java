package com.mmsm.streamingplatform.comment.commentrating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRatingRepository extends JpaRepository<CommentRating, Long> {
    @Query(
        value = "SELECT * FROM comment_rating WHERE comment_id = :commentId AND created_by_id = :ownerId",
        nativeQuery = true
    )
    Optional<CommentRating> findCommentRatingByCommentIdAndUserId(Long commentId, String ownerId);
}
