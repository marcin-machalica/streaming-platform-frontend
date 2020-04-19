package com.mmsm.streamingplatform.comment.commentrating.controller;

import com.mmsm.streamingplatform.comment.commentrating.model.CommentFavouriteDto;
import com.mmsm.streamingplatform.comment.commentrating.model.CommentRatingDto;
import com.mmsm.streamingplatform.comment.commentrating.service.CommentRatingService;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

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
        try {
            CommentRatingDto commentRatingDto = commentRatingService.upVoteComment(commentId, userId);
            return ControllerUtils.getFoundResponse(commentRatingDto);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/down-vote")
    public ResponseEntity<CommentRatingDto> downVoteComment(@PathVariable Long videoId,
                                                @PathVariable Long commentId,
                                                HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        try {
            CommentRatingDto commentRatingDto = commentRatingService.downVoteComment(commentId, userId);
            return ControllerUtils.getFoundResponse(commentRatingDto);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/favourite")
    public ResponseEntity<CommentFavouriteDto> favouriteComment(@PathVariable Long videoId,
                                                             @PathVariable Long commentId,
                                                             HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        try {
            CommentFavouriteDto commentFavouriteDto = commentRatingService.favouriteComment(videoId, commentId, userId);
            return ControllerUtils.getFoundResponse(commentFavouriteDto);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/pin")
    public ResponseEntity<Void> pinComment(@PathVariable Long videoId,
                                           @PathVariable Long commentId,
                                           HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        try {
            commentRatingService.pinComment(videoId, commentId, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (BadRequestException ex) {
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}