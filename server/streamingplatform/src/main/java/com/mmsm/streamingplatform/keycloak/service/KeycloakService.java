package com.mmsm.streamingplatform.keycloak.service;

import com.mmsm.streamingplatform.keycloak.mapper.KeycloakMapper;
import com.mmsm.streamingplatform.keycloak.model.UserDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakService {

    private final Keycloak keycloak;
    private final RealmResource realmResource;

    @Value("${keycloak-api-user-id}")
    private String API_USER_ID;

    @Value(("${keycloak-admin-group-id}"))
    private String ADMIN_GROUP_ID;

    KeycloakService(@Value("${keycloak.auth-server-url}") String serverUrl,
                    @Value("${keycloak.realm}") String realm,
                    @Value("${keycloak-api-username}") String username,
                    @Value("${keycloak-api-password}") String password,
                    @Value("${keycloak-api-client}") String clientId
                    ) {
        this.keycloak = Keycloak.getInstance(serverUrl, realm, username, password, clientId);
        this.realmResource = keycloak.realm(realm);
    }

    public List<UserDto> getAllUserDtos() {
        return realmResource.users().list().stream()
                .filter(userRepresentation -> !userRepresentation.getId().equals(API_USER_ID))
                .filter(userRepresentation -> !isAdmin(userRepresentation))
                .map(KeycloakMapper::getUserDtoFromUserRepresentation)
                .collect(Collectors.toList());
    }

    public UserDto getUserDtoById(String id) {
        if (id == null) {
            return null;
        }
        return KeycloakMapper.getUserDtoFromUserRepresentation(
                realmResource.users().get(id).toRepresentation()
        );
    }

    private boolean isAdmin(UserRepresentation userRepresentation) {
        return realmResource.groups().group(ADMIN_GROUP_ID).members().stream()
                .anyMatch(member -> member.getId().equals(userRepresentation.getId()));
    }
}
