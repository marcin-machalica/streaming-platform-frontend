package com.mmsm.streamingplatform.video;

import com.mmsm.streamingplatform.channel.Channel;
import com.mmsm.streamingplatform.channel.ChannelController.ChannelNotFoundByUserIdException;
import com.mmsm.streamingplatform.channel.ChannelRepository;
import com.mmsm.streamingplatform.utils.CommonExceptionsUtils.CanOnlyBePerformedByAuthorException;
import com.mmsm.streamingplatform.video.VideoController.*;
import com.mmsm.streamingplatform.comment.CommentController.*;
import com.mmsm.streamingplatform.comment.commentrating.CommentRating;
import com.mmsm.streamingplatform.comment.commentrating.CommentRatingRepository;
import com.mmsm.streamingplatform.comment.Comment;
import com.mmsm.streamingplatform.comment.CommentService;
import com.mmsm.streamingplatform.security.keycloak.KeycloakService;
import com.mmsm.streamingplatform.utils.FileUtils;
import com.mmsm.streamingplatform.video.videorating.VideoRating;
import com.mmsm.streamingplatform.video.videorating.VideoRatingController.*;
import com.mmsm.streamingplatform.video.videorating.VideoRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
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
        Channel channel = channelRepository.findByAuditorCreatedById(userId).orElseThrow(() -> new ChannelNotFoundByUserIdException(userId));

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
            throw new NotSupportedVideoFormatException();
        }
        String filename;
        do {
            filename = UUID.randomUUID().toString();
        } while (videoRepository.existsByFilename(filename));
        return filename + "." + fileExtension;
    }
}
