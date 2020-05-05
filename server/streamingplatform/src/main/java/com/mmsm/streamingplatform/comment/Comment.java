package com.mmsm.streamingplatform.comment;

import com.mmsm.streamingplatform.auditor.Auditor;
import com.mmsm.streamingplatform.comment.commentrating.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingController.*;
import com.mmsm.streamingplatform.comment.CommentController.*;
import com.mmsm.streamingplatform.keycloak.KeycloakController.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="id_sequence")
    private Long id;

    @NotNull
    @Size(min = 1, max = 5000)
    @Column(name = "message", nullable = false)
    String message;

    @NotNull
    @Column(name = "up_vote_count", nullable = false)
    private Long upVoteCount = 0L;

    @NotNull
    @Column(name = "down_vote_count", nullable = false)
    private Long downVoteCount = 0L;

    @NotNull
    @Column(name = "favourite_count", nullable = false)
    private Long favouriteCount = 0L;

    @NotNull
    @Column(name = "direct_replies_count", nullable = false)
    private Integer directRepliesCount = 0;

    @NotNull
    @Column(name = "all_replies_count", nullable = false)
    private Integer allRepliesCount = 0;

    @NotNull
    @Column(name = "is_video_author_favourite", nullable = false)
    private Boolean isVideoAuthorFavourite = false;

    @NotNull
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @NotNull
    @Column(name = "was_edited", nullable = false)
    private Boolean wasEdited = false;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> directReplies = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private List<CommentRating> commentRatings = new ArrayList<>();

    @Embedded
    private Auditor auditor;

    public static Comment of(String message) {
        return new Comment(null, message, 0L, 0L, 0L,
            0, 0, false, false,
            false, false, null, new ArrayList<>(), new ArrayList<>() , Auditor.of());
    }

    public CommentRepresentation toRepresentation(UserDto author, CommentRatingRepresentation currentUserCommentRating) {
        return new CommentRepresentation(id, parentComment != null ? parentComment.id : null, author, isDeleted ? null : message,
            upVoteCount, downVoteCount, favouriteCount, directRepliesCount, allRepliesCount, isVideoAuthorFavourite, isPinned,
            wasEdited, isDeleted, getCreatedDate(), currentUserCommentRating, null);
    }

    public static CommentRepresentation getCommentRepresentationWithReplies(CommentWithRepliesAndAuthors commentWithRepliesAndAuthors) {
        Comment comment = commentWithRepliesAndAuthors.getComment();

        return new CommentRepresentation(comment.getId(), comment.getParentComment() != null ? comment.getParentComment().id : null,
            commentWithRepliesAndAuthors.getAuthor(), comment.getIsDeleted() ? null : comment.getMessage(), comment.getUpVoteCount(),
            comment.getDownVoteCount(), comment.getFavouriteCount(), comment.getDirectRepliesCount(), comment.getAllRepliesCount(),
            comment.getIsVideoAuthorFavourite(), comment.getIsPinned(), comment.getWasEdited(), comment.getIsDeleted(),
            comment.getCreatedDate(), commentWithRepliesAndAuthors.getCommentRatingRepresentation(),
            getCommentRepresentationListWithReplies(commentWithRepliesAndAuthors.getCommentsAndAuthors()));
    }

    public static List<CommentRepresentation> getCommentRepresentationListWithReplies(List<CommentWithRepliesAndAuthors> commentsWithRepliesAndAuthors) {
        if (commentsWithRepliesAndAuthors == null) {
            return null;
        }
        List<CommentRepresentation> commentRepresentations = new ArrayList<>();
        for(CommentWithRepliesAndAuthors entity : commentsWithRepliesAndAuthors) {
            commentRepresentations.add(getCommentRepresentationWithReplies(entity));
        }
        return commentRepresentations;
    }

    public void addChildrenComment(Comment comment) {
        this.directReplies.add(comment);
        comment.setParentComment(this);
        directRepliesCount++;
        incrementParentsAllRepliesCount(this);
    }

    public Comment updateComment(CommentUpdate commentUpdate) {
        message = commentUpdate.getMessage();
        wasEdited = true;
        return this;
    }

    public Comment setDeleted() {
        isDeleted = true;
        return this;
    }

    public Comment upVote(CommentRating commentRating) {
        Boolean wasUpVote = commentRating.getIsUpVote();
        Boolean wasDownVote = commentRating.getIsDownVote();

        upVoteCount += wasUpVote ? -1 : 1;
        if (commentRating.getId() != null && wasDownVote) {
            downVoteCount -= 1;
        }
        return this;
    }

    public Comment downVote(CommentRating commentRating) {
        Boolean wasUpVote = commentRating.getIsUpVote();
        Boolean wasDownVote = commentRating.getIsDownVote();

        downVoteCount += wasDownVote ? -1 : 1;
        if (commentRating.getId() != null && wasUpVote) {
            upVoteCount -= 1;
        }
        return this;
    }

    public Comment favourite(CommentRating commentRating, boolean isVideoAuthorRating) {
        Boolean wasFavourite = commentRating.getIsFavourite();

        favouriteCount += wasFavourite ? -1 : 1;
        if (isVideoAuthorRating) {
            isVideoAuthorFavourite = !isVideoAuthorFavourite;
        }
        return this;
    }

    public CommentRating addCommentRating(CommentRating commentRating) {
        commentRatings.add(commentRating);
        return commentRating;
    }

    private void incrementParentsAllRepliesCount(Comment comment) {
        if (comment == null) {
            return;
        }
        comment.allRepliesCount++;
        incrementParentsAllRepliesCount(comment.getParentComment());
    }

    void setParentComment(Comment comment) {
        parentComment = comment;
    }

    public Instant getCreatedDate() {
        return auditor.getCreatedDate();
    }

    public String getCreatedById() {
        return auditor.getCreatedById();
    }

    public void setCreatedById(String id) {
        auditor.setCreatedById(id);
    }
}
