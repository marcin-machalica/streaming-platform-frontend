package com.mmsm.streamingplatform.security.keycloak;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/keycloak")
public class KeycloakController {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private String id;
        private String username;

        public static UserDto of(UserRepresentation userRepresentation) {
            return new UserDto(userRepresentation.getId(), userRepresentation.getUsername());
        }
    }

    private final KeycloakService keycloakService;

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return keycloakService.getAllUserDtos();
    }
}
