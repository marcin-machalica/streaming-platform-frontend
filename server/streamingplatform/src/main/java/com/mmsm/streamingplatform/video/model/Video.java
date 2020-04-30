package com.mmsm.streamingplatform.video.model;

import com.mmsm.streamingplatform.auditor.Auditor;
import com.mmsm.streamingplatform.comment.Comment;
import com.mmsm.streamingplatform.video.videorating.VideoRating;
import lombok.Getter;
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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video")
public class Video {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="id_sequence")
    private Long id;

    @NotNull
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 5000)
    @Column(name = "description")
    private String description;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "video_id")
    private List<VideoRating> videoRatings = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @Embedded
    private Auditor auditor;

    public Video upVote(VideoRating videoRating) {
        Boolean wasUpVote = videoRating.getIsUpVote();
        Boolean wasDownVote = videoRating.getIsDownVote();

        upVoteCount += wasUpVote ? -1 : 1;
        if (videoRating.getId() != null && wasDownVote) {
            downVoteCount -= 1;
        }
        return this;
    }

    public Video downVote(VideoRating videoRating) {
        Boolean wasUpVote = videoRating.getIsUpVote();
        Boolean wasDownVote = videoRating.getIsDownVote();

        downVoteCount += wasDownVote ? -1 : 1;
        if (videoRating.getId() != null && wasUpVote) {
            upVoteCount -= 1;
        }
        return this;
    }

    public VideoRating addVideoRating(VideoRating videoRating) {
        videoRatings.add(videoRating);
        return videoRating;
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

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
