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
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "video")
public class Video extends Auditor implements Serializable {

    // author todo

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    List<Comment> comments;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private VideoRating videoRating;
}
