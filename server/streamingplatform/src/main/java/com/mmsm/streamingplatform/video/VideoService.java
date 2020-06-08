package com.mmsm.streamingplatform.video;

import com.mmsm.streamingplatform.channel.Channel;
import com.mmsm.streamingplatform.channel.ChannelRepository;
import com.mmsm.streamingplatform.channel.ChannelService;
import com.mmsm.streamingplatform.video.VideoController.*;
import com.mmsm.streamingplatform.comment.CommentController.*;
import com.mmsm.streamingplatform.comment.commentrating.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingRepository;
import com.mmsm.streamingplatform.comment.Comment;
import com.mmsm.streamingplatform.comment.CommentService;
import com.mmsm.streamingplatform.keycloak.KeycloakController.UserDto;
import com.mmsm.streamingplatform.keycloak.KeycloakService;
import com.mmsm.streamingplatform.utils.FileUtils;
import com.mmsm.streamingplatform.video.videorating.VideoRating;
import com.mmsm.streamingplatform.video.videorating.VideoRatingController.*;
import com.mmsm.streamingplatform.video.videorating.VideoRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class ChannelNotFoundException extends RuntimeException {
        public ChannelNotFoundException(String userId) {
            super("Channel not found with userId: " + userId);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    static class VideoNotFoundException extends RuntimeException {
        VideoNotFoundException(Long id) {
            super("Video not found with id: " + id);
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    static class CanOnlyBePerformedByAuthorException extends RuntimeException {
        CanOnlyBePerformedByAuthorException() {
            super("This action can only be performed by the author");
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    static class NotSufficientPermissionsException extends RuntimeException {
        NotSufficientPermissionsException() {
            super("Not sufficient permissions to perform this operations");
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    static class CannotDeleteFileException extends RuntimeException {
        CannotDeleteFileException() {
            super("Error during deleting the file");
        }
    }

    private final KeycloakService keycloakService;
    private final VideoRepository videoRepository;
    private final VideoRatingRepository videoRatingRepository;
    private final CommentService commentService;
    private final CommentRatingRepository commentRatingRepository;
    private final ChannelRepository channelRepository;

    @Value("${VIDEOS_STORAGE_PATH}")
    public String VIDEOS_STORAGE_PATH;

    public List<VideoRepresentation> getAllVideos() {
        return videoRepository.findAll()
            .stream()
            .map(Video::toRepresentation)
            .collect(Collectors.toList());
    }

    public VideoDetails getVideoDetails(Long videoId, String userId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
        List<Comment> comments = video.getComments();

        Map<Comment, CommentRating> commentsAndRatings = new HashMap<>();
        comments.stream()
            .forEach(comment -> commentsAndRatings.put(
                comment,
                commentRatingRepository.findCommentRatingByCommentIdAndUserId(comment.getId(), userId).orElseGet(CommentRating::of)
            ));
        List<CommentWithRepliesAndAuthors> commentWithRepliesAndAuthors = commentService.getCommentsListWithRepliesAndAuthors(commentsAndRatings, userId);

        VideoRatingRepresentation videoRatingRepresentation = videoRatingRepository
            .findVideoRatingByVideoIdAndUserId(video.getId(), userId).orElseGet(VideoRating::of)
            .toRepresentation();
        return video.toVideoDetails(videoRatingRepresentation, commentWithRepliesAndAuthors);
    }

    @Transactional
    public VideoRepresentation createVideo(MultipartFile file, String title, String description, String userId) throws NotSupportedException, IOException {
        Channel channel = channelRepository.findByAuditorCreatedById(userId).orElseThrow(() -> new ChannelNotFoundException(userId));

        String filename = generateFilename(file.getOriginalFilename());
        Video video = Video.of(filename, title, description, channel);

        video = videoRepository.save(video);
        storeFile(file, filename);

        return video.toRepresentation();
    }

    public VideoRepresentation updateVideo(VideoUpdate videoUpdate, Long videoId, String userId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));

        if (!userId.equals(video.getCreatedById())) {
            throw new CanOnlyBePerformedByAuthorException();
        }

        video = video.updateVideo(videoUpdate);
        video = videoRepository.save(video);
        return video.toRepresentation();
    }

    @Transactional
    public void deleteVideoById(Long videoId, String userId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));

        if (!userId.equals(video.getCreatedById()) && !keycloakService.isAdmin(userId)) {
            throw new CannotDeleteFileException();
        }

        Path path = Path.of(VIDEOS_STORAGE_PATH + "/" + video.getFilename());
        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new CannotDeleteFileException();
        }
        videoRepository.delete(video);
    }

    public Pair<File, String> getFileAndFilenameWithExtension(Long videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
        return Pair.of(new File(VIDEOS_STORAGE_PATH + "/" + video.getFilename()), video.getTitle() + "." + FileUtils.getFileExtension(video.getFilename()));
    }

    private void storeFile(MultipartFile file, String filename) throws IOException {
        Path targetPath = Path.of(VIDEOS_STORAGE_PATH, "/", filename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String generateFilename(String originalFilename) throws NotSupportedException {
        String fileExtension = FileUtils.getFileExtension(originalFilename);
        if (!Arrays.asList(FileUtils.allowedFileFormatsCommaSeparated.split(",")).contains(fileExtension)) {
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
