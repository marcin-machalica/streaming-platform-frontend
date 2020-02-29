package com.mmsm.streamingplatform.keycloak.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private String id;
    private String username;
}
