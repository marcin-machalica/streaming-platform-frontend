package com.mmsm.streamingplatform.keycloak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class KeycloakTestController {
    @GetMapping(path = "/test1")
    public Map<String, String> test1() {
        return Collections.singletonMap("response", "1");
    }

    @GetMapping(path = "/admin/test1")
    public Map<String, String> adminTest1() {
        return Collections.singletonMap("response", "2");
    }
}
