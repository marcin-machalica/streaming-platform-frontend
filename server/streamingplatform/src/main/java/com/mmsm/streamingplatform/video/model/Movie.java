package com.mmsm.streamingplatform.video.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Movie {

    @Id
    private  Long id;
    private String path;

}
