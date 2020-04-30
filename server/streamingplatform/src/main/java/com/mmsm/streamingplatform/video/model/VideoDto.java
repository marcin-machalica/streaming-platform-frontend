package com.mmsm.streamingplatform.video.model;

import com.mmsm.streamingplatform.keycloak.KeycloakController.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class VideoDto {
    private Long id;
    private UserDto author;
    private String title;
    private String description;
    private Instant createdDate;
}
