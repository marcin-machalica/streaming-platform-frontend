package com.mmsm.streamingplatform.comment.commentrating.controller;

import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.commentrating.service.CommentRatingService;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@RestController
@RequestMapping("/videos/{videoId}/comments/{commentId}")
public class CommentRatingController {

    private final CommentRatingService commentRatingService;

    @PostMapping("/up-vote")
    public ResponseEntity<CommentRatingDto> upVoteComment(@PathVariable Long videoId,
                                                          @PathVariable Long commentId,
                                                          HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        CommentRatingDto commentRatingDto = commentRatingService.upVoteComment(commentId, userId);
        return ControllerUtils.getFoundResponse(commentRatingDto);
    }

    @PostMapping("/down-vote")
    public ResponseEntity<CommentRatingDto> downVoteComment(@PathVariable Long videoId,
                                                @PathVariable Long commentId,
                                                HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        CommentRatingDto commentRatingDto = commentRatingService.downVoteComment(commentId, userId);
        return ControllerUtils.getFoundResponse(commentRatingDto);
    }

    @PostMapping("/favourite")
    public ResponseEntity<CommentRatingDto> favouriteComment(@PathVariable Long videoId,
                                                 @PathVariable Long commentId,
                                                 HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        CommentRatingDto commentRatingDto = commentRatingService.favouriteComment(commentId, userId);
        return ControllerUtils.getFoundResponse(commentRatingDto);
    }
}