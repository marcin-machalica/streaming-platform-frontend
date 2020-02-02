package com.mmsm.streamingplatform.video.controller;

import com.mmsm.streamingplatform.video.model.Movie;
import com.mmsm.streamingplatform.video.service.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/video")
public class MovieDownloadController {

    private final MovieRepository movieRepository;

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> getMeasurement(@PathVariable("id") Long id) throws FileNotFoundException {
        Optional<Movie> movie = movieRepository.findById(id);
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
