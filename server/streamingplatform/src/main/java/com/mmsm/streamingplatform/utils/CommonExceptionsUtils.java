package com.mmsm.streamingplatform.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CommonExceptionsUtils {

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public static class CanOnlyBePerformedByAuthorException extends RuntimeException {
        public CanOnlyBePerformedByAuthorException() {
            super("Only author can perform this operation");
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public static class NotSufficientPermissionsException extends RuntimeException {
        public NotSufficientPermissionsException() {
            super("Not sufficient permissions to perform this operation");
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public static class CannotBePerformedByAuthorException extends RuntimeException {
        public CannotBePerformedByAuthorException() {
            super("Author cannot perform this operation");
        }
    }
}
