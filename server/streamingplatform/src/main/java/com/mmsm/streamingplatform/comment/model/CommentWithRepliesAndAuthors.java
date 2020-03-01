package com.mmsm.streamingplatform.comment.model;

import com.mmsm.streamingplatform.keycloak.model.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentWithRepliesAndAuthors {
    Comment comment;
    UserDto author;
    List<CommentWithRepliesAndAuthors> commentsAndAuthors;
}
