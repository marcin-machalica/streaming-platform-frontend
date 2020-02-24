package com.mmsm.streamingplatform.comment.model;

import com.mmsm.streamingplatform.auditor.Auditor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "comment")
public class Comment extends Auditor implements Serializable {

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
    private Long favouriteVoteCount = 0L;

    @NotNull
    @Column(name = "replies_count", nullable = false)
    private Integer repliesCount = 0;

    @NotNull
    @Column(name = "is_video_author_favourite", nullable = false)
    private Boolean isVideoAuthorFavourite = false;

    @NotNull
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @ManyToOne
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<Comment> directReplies;

    // author todo

    public void addChildrenComment(Comment comment) {
        this.directReplies.add(comment);
        comment.setParentComment(this);
        repliesCount++;
    }
}
