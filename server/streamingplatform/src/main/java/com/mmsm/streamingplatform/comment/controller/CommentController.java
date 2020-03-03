package com.mmsm.streamingplatform.comment.controller;

import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.comment.service.CommentService;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@AllArgsConstructor
@RestController
@RequestMapping("/videos/{videoId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentDtoWithReplies(@PathVariable Long videoId, @PathVariable Long commentId) {
        CommentDto commentDto = commentService.getCommentDtoWithReplies(commentId);
        return ControllerUtils.getFoundResponse(commentDto);
    }

    @PostMapping("")
    public ResponseEntity<CommentDto> saveCommentDto(@RequestBody CommentDto commentDto,
                                                     @PathVariable Long videoId) throws URISyntaxException {
        CommentDto savedCommentDto = commentService.saveComment(commentDto, videoId);
        URI uri = savedCommentDto != null ? new URI("/api/v1/videos/" + videoId + "/comments/" + savedCommentDto.getId()) : null;
        return ControllerUtils.getCreatedResponse(savedCommentDto, uri);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@RequestBody CommentDto commentDto,
                                                    @PathVariable Long videoId,
                                                    @PathVariable Long commentId,
                                                    HttpServletRequest request) {
        String authorId = SecurityUtils.getUserIdFromRequest(request);
        CommentDto savedCommentDto = commentService.updateComment(commentDto, authorId, commentId);
        return ControllerUtils.getUpdatedResponse(savedCommentDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long videoId,
                                              @PathVariable Long commentId,
                                              HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        boolean isDeleted = commentService.deleteCommentById(videoId, commentId, userId);
        return ControllerUtils.getDeletedResponse(isDeleted);
    }
}