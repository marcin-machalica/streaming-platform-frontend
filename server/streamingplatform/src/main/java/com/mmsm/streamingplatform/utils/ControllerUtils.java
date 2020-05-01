package com.mmsm.streamingplatform.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ControllerUtils {

    public static <T> ResponseEntity<T> getCreatedResponse(T resource, URI uri) {
        if (resource == null || uri == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.created(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Access-Control-Expose-Headers", HttpHeaders.LOCATION)
            .body(resource);
    }
}
