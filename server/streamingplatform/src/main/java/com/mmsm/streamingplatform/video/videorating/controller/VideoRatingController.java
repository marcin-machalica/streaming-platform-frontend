package com.mmsm.streamingplatform.video.videorating.controller;

import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import com.mmsm.streamingplatform.video.videorating.model.VideoRatingDto;
import com.mmsm.streamingplatform.video.videorating.service.VideoRatingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

@AllArgsConstructor
@RestController
@RequestMapping("/videos/{videoId}")
public class VideoRatingController {

    private final VideoRatingService videoRatingService;

    @PostMapping("/up-vote")
    public ResponseEntity<VideoRatingDto> upVoteComment(@PathVariable Long videoId,
                                                          HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        try {
            VideoRatingDto videoRatingDto = videoRatingService.upVoteVideo(videoId, userId);
            return ControllerUtils.getFoundResponse(videoRatingDto);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/down-vote")
    public ResponseEntity<VideoRatingDto> downVoteComment(@PathVariable Long videoId,
                                                HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        try {
            VideoRatingDto videoRatingDto = videoRatingService.downVoteVideo(videoId, userId);
            return ControllerUtils.getFoundResponse(videoRatingDto);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}