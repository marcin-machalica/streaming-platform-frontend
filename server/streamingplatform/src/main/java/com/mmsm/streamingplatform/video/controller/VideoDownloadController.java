package com.mmsm.streamingplatform.video.controller;

import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDto;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import com.mmsm.streamingplatform.video.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
public class VideoDownloadController {

    private final VideoRepository videoRepository;
    private final VideoService videoService;

    @GetMapping("/videos")
    public List<VideoDto> getAllMovies() {
        return videoService.getAllMovieDtos();
    }

    @GetMapping("/videosJSON")
    public List<Video> getAllMoviesJSON() {
        return videoRepository.findAll();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> getMeasurement(@PathVariable("id") Long id) throws FileNotFoundException {
        Optional<Video> movie = videoRepository.findById(id);
        if (!movie.isPresent()) {
            return null;
        }
        
        File file = new File(movie.get().getPath());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
