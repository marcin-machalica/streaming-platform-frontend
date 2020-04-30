package com.mmsm.streamingplatform.video.videorating;

import com.mmsm.streamingplatform.auditor.Auditor;
import com.mmsm.streamingplatform.video.videorating.VideoRatingController.*;
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

    public static VideoRating of() {
        return new VideoRating(null, false, false, Auditor.of());
    }

    public VideoRatingRepresentation toRepresentation() {
        return new VideoRatingRepresentation(isUpVote, isDownVote);
    }

    VideoRating upVote() {
        isUpVote = !isUpVote;
        isDownVote = false;
        return this;
    }

    VideoRating downVote() {
        isDownVote = !isDownVote;
        isUpVote = false;
        return this;
    }
}
