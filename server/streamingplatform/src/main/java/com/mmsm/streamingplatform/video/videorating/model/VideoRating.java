package com.mmsm.streamingplatform.video.videorating.model;

import com.mmsm.streamingplatform.auditor.Auditor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video_rating")
public class VideoRating {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="id_sequence")
    private Long id;

    @NotNull
    @Column(name = "is_up_vote", nullable = false)
    private Boolean isUpVote = false;

    @NotNull
    @Column(name = "is_down_vote", nullable = false)
    private Boolean isDownVote = false;

    @Embedded
    private Auditor auditor;
}
