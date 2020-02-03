package com.mmsm.streamingplatform.video.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class Video {

    @Id
    private Long id;
    private String path;
    private Date created_at;
}
