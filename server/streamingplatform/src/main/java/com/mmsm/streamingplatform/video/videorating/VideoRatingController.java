package com.mmsm.streamingplatform.video.videorating;

import com.mmsm.streamingplatform.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@RestController
@RequestMapping("/videos/{videoId}")
public class VideoRatingController {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoRatingRepresentation {
        private Boolean isUpVote;
        private Boolean isDownVote;
    }

    private final VideoRatingService videoRatingService;

    @PostMapping("/up-vote")
    public VideoRatingRepresentation upVoteComment(@PathVariable Long videoId, HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        return videoRatingService.upVoteVideo(videoId, userId);
    }

    @PostMapping("/down-vote")
    public VideoRatingRepresentation downVoteComment(@PathVariable Long videoId, HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        return videoRatingService.downVoteVideo(videoId, userId);
    }
}