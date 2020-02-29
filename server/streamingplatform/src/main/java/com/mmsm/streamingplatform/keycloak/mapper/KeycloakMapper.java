package com.mmsm.streamingplatform.keycloak.mapper;

import com.mmsm.streamingplatform.keycloak.model.UserDto;
import org.keycloak.representations.idm.UserRepresentation;

public class KeycloakMapper {

    public static UserDto getUserDtoFromUserRepresentation(UserRepresentation userRepresentation) {
        if (userRepresentation == null) {
            return null;
        }
        return UserDto.builder()
                .id(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .build();
    }
}
