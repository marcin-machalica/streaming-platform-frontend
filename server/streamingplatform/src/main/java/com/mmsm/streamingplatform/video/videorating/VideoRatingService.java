package com.mmsm.streamingplatform.video.videorating;

import com.mmsm.streamingplatform.video.Video;
import com.mmsm.streamingplatform.video.VideoRepository;
import com.mmsm.streamingplatform.video.videorating.VideoRatingController.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

@RequiredArgsConstructor
@Service
public class VideoRatingService {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    static class VideoNotFoundException extends RuntimeException {
        VideoNotFoundException(Long id) {
            super("Video not found with id: " + id);
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    static class CannotBePerformedByAuthorException extends RuntimeException {
        CannotBePerformedByAuthorException() {
            super("This action cannot be performed by the author");
        }
    }
    
    private final VideoRepository videoRepository;
    private final VideoRatingRepository videoRatingRepository;

    @Transactional
    public VideoRatingRepresentation upVoteVideo(Long videoId, String userId) throws NotFoundException, NotAuthorizedException {
        Video video = getVideoIfNotAuthor(videoId, userId);
        VideoRating videoRating = videoRatingRepository.findVideoRatingByVideoIdAndUserId(videoId, userId).orElseGet(VideoRating::of);
        videoRating = videoRating.getId() != null ? videoRating : video.addVideoRating(videoRating);

        video = video.upVote(videoRating);
        videoRating = videoRating.upVote();

        videoRepository.save(video);
        videoRating = videoRatingRepository.save(videoRating);
        return videoRating.toRepresentation();
    }

    @Transactional
    public VideoRatingRepresentation downVoteVideo(Long videoId, String userId) throws NotFoundException, NotAuthorizedException {
        Video video = getVideoIfNotAuthor(videoId, userId);
        VideoRating videoRating = videoRatingRepository.findVideoRatingByVideoIdAndUserId(videoId, userId).orElseGet(VideoRating::of);
        videoRating = videoRating.getId() != null ? videoRating : video.addVideoRating(videoRating);

        video = video.downVote(videoRating);
        videoRating = videoRating.downVote();

        videoRepository.save(video);
        videoRating = videoRatingRepository.save(videoRating);
        return videoRating.toRepresentation();
    }

    @Transactional
    Video getVideoIfNotAuthor(Long videoId, String userId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
        if (video.getCreatedById().equals(userId)) {
            throw new CannotBePerformedByAuthorException();
        }
        return video;
    }
}
