package com.mmsm.streamingplatform.video.model;

import com.mmsm.streamingplatform.auditor.Auditor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class Video extends Auditor implements Serializable {

    @NotNull
    @Column(name = "path", nullable = false)
    private String path;
}
