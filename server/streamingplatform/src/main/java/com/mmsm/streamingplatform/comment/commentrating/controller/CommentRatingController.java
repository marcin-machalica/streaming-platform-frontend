package com.mmsm.streamingplatform.comment.commentrating.controller;

import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.commentrating.service.CommentRatingService;
import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

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
    public ResponseEntity<CommentDto> downVoteComment(@RequestBody CommentDto commentDto,
                                                     @PathVariable Long videoId) throws URISyntaxException {
        // todo
        return ControllerUtils.getFoundResponse(commentDto);
    }

    @PostMapping("/favourite")
    public ResponseEntity<CommentDto> favouriteComment(@RequestBody CommentDto commentDto,
                                                     @PathVariable Long videoId) throws URISyntaxException {
        // todo
        return ControllerUtils.getFoundResponse(commentDto);
    }
}