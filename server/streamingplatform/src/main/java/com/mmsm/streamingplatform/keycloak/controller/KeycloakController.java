package com.mmsm.streamingplatform.keycloak.controller;

import com.mmsm.streamingplatform.keycloak.model.UserDto;
import com.mmsm.streamingplatform.keycloak.service.KeycloakService;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/keycloak")
public class KeycloakController {

    private final KeycloakService keycloakService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUserDtos() {
        List<UserDto> allUserDtos = keycloakService.getAllUserDtos();
        return ControllerUtils.getFoundResponse(allUserDtos);
    }
}
