package com.mmsm.streamingplatform.video.model;

import com.mmsm.streamingplatform.auditor.Auditor;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.video.videorating.model.VideoRating;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "video")
public class Video extends Auditor implements Serializable {

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
    List<Comment> comments;
}
