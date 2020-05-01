package com.mmsm.streamingplatform.video;

import com.mmsm.streamingplatform.comment.CommentController;
import com.mmsm.streamingplatform.keycloak.KeycloakController;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import com.mmsm.streamingplatform.video.videorating.VideoRatingController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.NotSupportedException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/videos")
public class VideoController {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class VideoDetails {
        private Long id;
        private KeycloakController.UserDto author;
        private String title;
        private String description;
        private Long upVoteCount;
        private Long downVoteCount;
        private Long viewCount;
        private Long shareCount;
        private Instant createdDate;
        private VideoRatingController.VideoRatingRepresentation currentUserVideoRating;
        private List<CommentController.CommentRepresentation> directCommentDtos;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class VideoRepresentation {
        private Long id;
        private KeycloakController.UserDto author;
        private String title;
        private String description;
        private Instant createdDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateVideo {
        private String description;
    }

    private final VideoService videoService;

    @GetMapping
    public List<VideoRepresentation> getAllVideos() {
        return videoService.getAllVideos();
    }

    @GetMapping("/{videoId}")
    public VideoDetails getVideoDetailsDtoById(@PathVariable Long videoId, HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        return videoService.getVideoDetails(videoId, userId);
    }

    @PostMapping
    public ResponseEntity<VideoRepresentation> createVideoDetailsDto(@RequestParam MultipartFile file, @RequestParam String title,
                                                                     @RequestParam String description) throws URISyntaxException, IOException, NotSupportedException {
        VideoRepresentation savedVideo = videoService.createVideo(file, title, description);
        URI uri = new URI("/api/v1/videos/" + savedVideo.getId());
        return ControllerUtils.getCreatedResponse(savedVideo, uri);
    }

    @PutMapping("/{videoId}")
    public VideoRepresentation updateVideoDetailsDto(@RequestBody UpdateVideo updateVideo, @PathVariable Long id,
                                                     HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        return videoService.updateVideo(updateVideo, userId, id);
    }

    @DeleteMapping("/{videoId}")
    public void deleteVideoById(@PathVariable Long videoId, HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        videoService.deleteVideoById(videoId, userId);
    }

    @GetMapping("/{videoId}/download")
    public ResponseEntity<InputStreamResource> downloadVideoById(@PathVariable Long videoId) throws FileNotFoundException {
        Pair<File, String> fileAndFilename = videoService.getFileAndFilenameWithExtension(videoId);
        File file = fileAndFilename.getFirst();
        String filename = fileAndFilename.getSecond();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
            .contentLength(file.length())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header("Content-Disposition", "attachment; filename=" + filename)
            .body(resource);
    }
}
