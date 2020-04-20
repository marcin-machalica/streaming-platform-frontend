package com.mmsm.streamingplatform.comment.model;

import com.mmsm.streamingplatform.auditor.Auditor;
import com.mmsm.streamingplatform.comment.commentrating.CommentRating;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
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

    public void addChildrenComment(Comment comment) {
        this.directReplies.add(comment);
        comment.setParentComment(this);
        directRepliesCount++;
        incrementParentsAllRepliesCount(this);
    }

    private void incrementParentsAllRepliesCount(Comment comment) {
        if (comment == null) {
            return;
        }
        comment.allRepliesCount++;
        incrementParentsAllRepliesCount(comment.getParentComment());
    }
}
