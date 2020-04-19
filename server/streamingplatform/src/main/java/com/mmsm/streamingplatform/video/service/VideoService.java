package com.mmsm.streamingplatform.video.service;

import com.mmsm.streamingplatform.comment.commentrating.model.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.service.CommentRatingRepository;
import com.mmsm.streamingplatform.comment.model.Comment;
import com.mmsm.streamingplatform.comment.model.CommentWithRepliesAndAuthors;
import com.mmsm.streamingplatform.comment.service.CommentService;
import com.mmsm.streamingplatform.keycloak.model.UserDto;
import com.mmsm.streamingplatform.keycloak.service.KeycloakService;
import com.mmsm.streamingplatform.utils.FileUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import com.mmsm.streamingplatform.video.mapper.VideoMapper;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDetailsDto;
import com.mmsm.streamingplatform.video.model.VideoDto;
import com.mmsm.streamingplatform.video.videorating.mapper.VideoRatingMapper;
import com.mmsm.streamingplatform.video.videorating.model.VideoRating;
import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;
import com.mmsm.streamingplatform.video.videorating.service.VideoRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.NotSupportedException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VideoService {

    private final KeycloakService keycloakService;
    private final VideoRepository videoRepository;
    private final VideoRatingRepository videoRatingRepository;
    private final CommentService commentService;
    private final CommentRatingRepository commentRatingRepository;

    @Value("${VIDEOS_STORAGE_PATH}")
    public String VIDEOS_STORAGE_PATH;

    public List<VideoDto> getAllVideoDtos() {
        return videoRepository.findAll()
                .stream()
                .map(video -> VideoMapper.getVideoDtoFromEntity(
                        video,
                        keycloakService.getUserDtoById(video.getCreatedById())
                ))
                .collect(Collectors.toList());
    }

    public VideoDetailsDto getVideoDetailsDtoByVideoId(Long videoId, String userId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isEmpty()) {
            return null;
        }
        Video video = videoOptional.get();
        List<Comment> comments = video.getComments();

        Map<Comment, CommentRating> commentsAndRatings = new HashMap<>();
        comments.stream()
                .forEach(comment -> commentsAndRatings.put(
                        comment,
                        commentRatingRepository.findCommentRatingByCommentIdAndUserId(comment.getId(), userId).orElseGet(CommentRating::new)
                ));
        List<CommentWithRepliesAndAuthors> commentWithRepliesAndAuthors = commentService.getCommentsWithRepliesAndAuthors(commentsAndRatings, userId);
        UserDto videoAuthor = keycloakService.getUserDtoById(video.getCreatedById());

        VideoRating videoRating = videoRatingRepository.findVideoRatingByVideoIdAndUserId(video.getId(), userId).orElseGet(VideoRating::new);
        VideoRatingDto videoRatingDto = VideoRatingMapper.getVideoRatingDtoFromEntity(videoRating);

        return videoOptional
                .map(foundVideo -> VideoMapper.getVideoDetailsDtoFromEntity(foundVideo, videoAuthor, videoRatingDto, commentWithRepliesAndAuthors))
                .orElse(null);
    }

    @Transactional
    public VideoDto createVideo(MultipartFile file, String title, String description) throws NotSupportedException, IOException {
        String filename = generateFilename(file.getOriginalFilename());
        Video video = new Video();
        video.setFilename(filename);
        video.setTitle(title);
        video.setDescription(description);
        Video savedVideo = videoRepository.save(video);
        storeFile(file, filename);
        UserDto author = keycloakService.getUserDtoById(savedVideo.getCreatedById());
        return VideoMapper.getVideoDtoFromEntity(savedVideo, author);
    }

    public VideoDto updateVideoDto(VideoDto dto, Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (dto == null || videoOptional.isEmpty()) {
            return null;
        }
        Video video = videoOptional.get();
        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        Video updatedVideo = videoRepository.save(video);
        UserDto author = keycloakService.getUserDtoById(updatedVideo.getCreatedById());
        return VideoMapper.getVideoDtoFromEntity(updatedVideo, author);
    }

    @Transactional
    public boolean deleteVideoById(Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        Optional<String> currentUserOptional = SecurityUtils.getCurrentUserId();
        if (videoOptional.isPresent() && currentUserOptional.isPresent() &&
           (SecurityUtils.hasAdminRole() || currentUserOptional.get().equals(videoOptional.get().getCreatedById()))) {

            Video video = videoOptional.get();
            videoRepository.delete(video);
            File file = new File(VIDEOS_STORAGE_PATH + "/" + video.getFilename());
            return file.delete();
        }
        return false;
    }

    public File downloadFile(Long videoId) {
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) {
            return null;
        }
        File file = new File(VIDEOS_STORAGE_PATH + "/" + video.get().getFilename());
        return file;
    }

    private void storeFile(MultipartFile file, String filename) throws IOException {
        Path targetPath = Path.of(VIDEOS_STORAGE_PATH, "/", filename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String generateFilename(String originalFilename) throws NotSupportedException {
        String fileExtension = FileUtils.getFileExtension(originalFilename);
        if (!Arrays.asList(FileUtils.allowedFileFormatsCommaSeparated
                        .split(","))
                    .contains(fileExtension)) {
            throw new NotSupportedException("Not supported video extension");
        }
        String filename;
        do {
            filename = UUID.randomUUID().toString();
        } while (doFileExists(filename));
        return filename + "." + fileExtension;
    }

    private boolean doFileExists(String uuid) {
        return videoRepository.getVideoCountByFilename(uuid) != 0;
    }
}
