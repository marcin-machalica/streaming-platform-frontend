package com.mmsm.streamingplatform.utils;

import java.util.Optional;

public class FileUtils {

    public static final String allowedFileFormatsCommaSeparated = "mp4,webm";

    public static String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .map(String::toLowerCase)
                .orElse("");
    }
}
