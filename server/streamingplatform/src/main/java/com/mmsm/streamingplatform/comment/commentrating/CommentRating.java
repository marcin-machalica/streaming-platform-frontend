package com.mmsm.streamingplatform.comment.commentrating;

import com.mmsm.streamingplatform.auditor.Auditor;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentRatingRepresentation;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.CommentFavouriteDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comment_rating")
public class CommentRating {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="id_sequence")
    private Long id;

    @NotNull
    @Column(name = "is_up_vote", nullable = false)
    private Boolean isUpVote = false;

    @NotNull
    @Column(name = "is_down_vote", nullable = false)
    private Boolean isDownVote = false;

    @NotNull
    @Column(name = "is_favourite", nullable = false)
    private Boolean isFavourite = false;

    @Embedded
    private Auditor auditor;

    static CommentRating of() {
        return new CommentRating(null, false, false, false, Auditor.of());
    }

    public CommentRatingRepresentation toCommentRatingRepresentation(Long commentId) {
        return new CommentRatingRepresentation(commentId, isUpVote, isDownVote, isFavourite);
    }

    CommentFavouriteDto toCommentFavouriteDto(Long favouriteCount, Boolean isVideoAuthorFavourite) {
        return new CommentFavouriteDto(favouriteCount, isFavourite, isVideoAuthorFavourite);
    }

    CommentRating upVote() {
        isUpVote = !isUpVote;
        isDownVote = false;
        return this;
    }

    CommentRating downVote() {
        isDownVote = !isDownVote;
        isUpVote = false;
        return this;
    }

    CommentRating favourite() {
        isFavourite = !isFavourite;
        return this;
    }
}
