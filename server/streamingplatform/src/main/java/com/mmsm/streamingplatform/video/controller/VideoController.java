package com.mmsm.streamingplatform.video.controller;

import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import com.mmsm.streamingplatform.video.model.VideoDetailsDto;
import com.mmsm.streamingplatform.video.model.VideoDto;
import com.mmsm.streamingplatform.video.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.NotSupportedException;
import java.io.*;
import java.net.URI;
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
    public ResponseEntity<VideoDetailsDto> getVideoDetailsDtoById(@PathVariable Long id,
                                                                  HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        VideoDetailsDto videoDetailsDto = videoService.getVideoDetailsDtoByVideoId(id, userId);
        return ControllerUtils.getFoundResponse(videoDetailsDto);
    }

    @PostMapping(value = "")
    public ResponseEntity<VideoDto> createVideoDetailsDto(@RequestParam MultipartFile file,
                                                          @RequestParam String title,
                                                          @RequestParam String description) {
        try {
            VideoDto savedVideoDto = videoService.createVideo(file, title, description);
            URI uri = new URI("/api/v1/videos/" + savedVideoDto.getId());
            return ControllerUtils.getCreatedResponse(savedVideoDto, uri);
        } catch (NotSupportedException ex) {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
