package org.houzz.social.comment.controller;

import jakarta.validation.Valid;
import org.houzz.social.auth.dto.AuthenticatedUser;
import org.houzz.social.comment.dto.CommentDto;
import org.houzz.social.comment.dto.CreateCommentRequest;
import org.houzz.social.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> list(@PathVariable Long postId) {
        return commentService.list(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(@AuthenticationPrincipal AuthenticatedUser user,
                          @PathVariable Long postId,
                          @Valid @RequestBody CreateCommentRequest request) {
        return commentService.add(postId, user.id(), request);
    }
}
