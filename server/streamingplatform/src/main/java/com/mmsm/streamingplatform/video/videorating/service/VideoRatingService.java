package com.mmsm.streamingplatform.video.videorating.service;

import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import com.mmsm.streamingplatform.video.videorating.mapper.VideoRatingMapper;
import com.mmsm.streamingplatform.video.videorating.model.VideoRating;
import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VideoRatingService {
    
    private final VideoRepository videoRepository;
    private final VideoRatingRepository videoRatingRepository;

    @Transactional
    public VideoRatingDto upVoteVideo(Long videoId, String userId) throws NotFoundException, NotAuthorizedException {
        Video video = getVideoOrThrowIfNotFoundOrUserIsAuthor(videoId, userId);
        VideoRating videoRating = videoRatingRepository.findVideoRatingByVideoIdAndUserId(videoId, userId).orElseGet(VideoRating::new);
        Boolean wasUpVote = videoRating.getIsUpVote();
        videoRating.setIsUpVote(!wasUpVote);
        video.setUpVoteCount(video.getUpVoteCount() + (wasUpVote ? -1 : 1));

        if (videoRating.getId() != null) {
            Boolean wasDownVote = videoRating.getIsDownVote();
            if (wasDownVote) {
                videoRating.setIsDownVote(false);
                video.setDownVoteCount(video.getDownVoteCount() - 1);
            }
        } else {
            video.getVideoRatings().add(videoRating);
        }

        videoRating = videoRatingRepository.save(videoRating);
        return VideoRatingMapper.getVideoRatingDtoFromEntity(videoRating);
    }

    @Transactional
    public VideoRatingDto downVoteVideo(Long videoId, String userId) throws NotFoundException, NotAuthorizedException {
        Video video = getVideoOrThrowIfNotFoundOrUserIsAuthor(videoId, userId);
        VideoRating videoRating = videoRatingRepository.findVideoRatingByVideoIdAndUserId(videoId, userId).orElseGet(VideoRating::new);
        Boolean wasDownVote = videoRating.getIsDownVote();
        videoRating.setIsDownVote(!wasDownVote);
        video.setDownVoteCount(video.getDownVoteCount() + (wasDownVote ? -1 : 1));

        if (videoRating.getId() != null) {
            Boolean wasUpVote = videoRating.getIsUpVote();
            if (wasUpVote) {
                videoRating.setIsUpVote(false);
                video.setUpVoteCount(video.getUpVoteCount() - 1);
            }
        } else {
            video.getVideoRatings().add(videoRating);
        }

        videoRating = videoRatingRepository.save(videoRating);
        return VideoRatingMapper.getVideoRatingDtoFromEntity(videoRating);
    }

    @Transactional
    Video getVideoOrThrowIfNotFoundOrUserIsAuthor(Long videoId, String userId) throws NotFoundException, NotAuthorizedException {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isEmpty()) {
            throw new NotFoundException();
        }
        Video video = videoOptional.get();
        if (video.getCreatedById().equals(userId)) {
            throw new NotAuthorizedException("");
        }
        return video;
    }
}
