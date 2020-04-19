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
    @Column(name = "is_up_vote", nullable = false)
    private Boolean isUpVote = false;

    @NotNull
    @Column(name = "is_down_vote", nullable = false)
    private Boolean isDownVote = false;
}
