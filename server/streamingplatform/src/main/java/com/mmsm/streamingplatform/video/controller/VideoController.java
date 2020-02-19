package com.mmsm.streamingplatform.video.controller;

import com.mmsm.streamingplatform.utils.Constants;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDetailsDto;
import com.mmsm.streamingplatform.video.model.VideoDto;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import com.mmsm.streamingplatform.video.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoRepository videoRepository;
    private final VideoService videoService;

    @GetMapping("")
    public ResponseEntity<List<VideoDto>> getAllVideos() {
        List<VideoDto> allVideoDtos = videoService.getAllVideoDtos();
        return ControllerUtils.getFoundResponse(allVideoDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDetailsDto> getVideoDetailsDtoById(@PathVariable Long id) {
        VideoDetailsDto videoDetailsDto = videoService.getVideoDetailsDtoByVideoId(id);
        return ControllerUtils.getFoundResponse(videoDetailsDto);
    }

    @PostMapping("")
    public ResponseEntity<VideoDto> createVideoDetailsDto(@RequestBody VideoDto videoDto) throws URISyntaxException {
        VideoDto savedVideoDto = videoService.saveVideoDto(videoDto);
        URI uri = (savedVideoDto != null && savedVideoDto.getId() != null)
                ? new URI("/api/v1/videos/" + savedVideoDto.getId()) : null;
        return ControllerUtils.getCreatedResponse(savedVideoDto, uri);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoDto> updateVideoDetailsDto(@RequestBody VideoDto videoDto, @PathVariable Long id) {
        VideoDto updatedVideoDto = videoService.updateVideoDto(videoDto, id);
        return ControllerUtils.getUpdatedResponse(updatedVideoDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoById(@PathVariable Long id) {
        boolean isDeleted = videoService.deleteVideoById(id);
        return ControllerUtils.getDeletedResponse(isDeleted);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadVideoById(@PathVariable Long id) {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isEmpty()) {
            return null;
        }

        try {
            File file = new File(Constants.VIDEOS_STORAGE_PATH + "/" + video.get().getFilename());
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
