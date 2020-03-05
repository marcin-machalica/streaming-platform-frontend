package com.mmsm.streamingplatform.comment.commentrating.model;

import com.mmsm.streamingplatform.auditor.Auditor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "comment_rating")
public class CommentRating extends Auditor implements Serializable {

    @NotNull
    @Column(name = "is_up_vote", nullable = false)
    private Boolean isUpVote = false;

    @NotNull
    @Column(name = "is_down_vote", nullable = false)
    private Boolean isDownVote = false;

    @NotNull
    @Column(name = "is_favourite", nullable = false)
    private Boolean isFavourite = false;
}
