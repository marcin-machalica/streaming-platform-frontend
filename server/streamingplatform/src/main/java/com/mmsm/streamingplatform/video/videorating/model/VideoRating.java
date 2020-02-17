package com.mmsm.streamingplatform.video.videorating.model;

import com.mmsm.streamingplatform.auditor.Auditor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "video_rating")
public class VideoRating extends Auditor implements Serializable {

    @NotNull
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @NotNull
    @Column(name = "share_count", nullable = false)
    private Long shareCount = 0L;

    @NotNull
    @Column(name = "up_vote_count", nullable = false)
    private Long upVoteCount = 0L;

    @NotNull
    @Column(name = "down_vote_count", nullable = false)
    private Long downVoteCount = 0L;
}
