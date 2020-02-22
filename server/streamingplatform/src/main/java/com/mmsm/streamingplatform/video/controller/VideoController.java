package com.mmsm.streamingplatform.video.controller;

import com.mmsm.streamingplatform.utils.ControllerUtils;
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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.NotSupportedException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/videos")
public class VideoController {

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

    @PostMapping(value = "{id}/upload")
    public ResponseEntity<Void> uploadVideo(@RequestParam MultipartFile file, @PathVariable Long id) {
        try {
            videoService.uploadFile(file, id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotSupportedException ex) {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}/download")
    public ResponseEntity<InputStreamResource> downloadVideoById(@PathVariable Long id) {
        try {
            File file = videoService.downloadFile(id);
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
