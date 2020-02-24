package com.mmsm.streamingplatform.comment.controller;

import com.mmsm.streamingplatform.comment.model.CommentDto;
import com.mmsm.streamingplatform.comment.service.CommentService;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@AllArgsConstructor
@RestController
@RequestMapping("/videos/{videoId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentDtoWithReplies(@PathVariable Long videoId, @PathVariable Long commentId) {
        CommentDto commentDto = commentService.getCommentWithReplyDtos(commentId);
        return ControllerUtils.getFoundResponse(commentDto);
    }

    @PostMapping("")
    public ResponseEntity<CommentDto> saveCommentDto(@RequestBody CommentDto commentDto, @PathVariable Long videoId) throws URISyntaxException {
        CommentDto savedCommentDto = commentService.saveComment(commentDto, videoId);
        URI uri = savedCommentDto != null ? new URI("/api/v1/videos/" + videoId + "/comments/" + savedCommentDto.getId()) : null;
        return ControllerUtils.getCreatedResponse(savedCommentDto, uri);
    }
}