package com.mmsm.streamingplatform.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ControllerUtils {
    public static <T> ResponseEntity<T> getFoundResponse(T resource) {
        if (resource == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);
    }

    public static <T> ResponseEntity<T> getCreatedResponse(T resource, URI uri) {
        if (resource == null || uri == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.created(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);
    }

    public static <T> ResponseEntity<T> getUpdatedResponse(T resource) {
        if (resource == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);
    }

    public static <T> ResponseEntity<T> getDeletedResponse(boolean isDeleted) {
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
