package org.houzz.social.like.controller;

import org.houzz.social.auth.dto.AuthenticatedUser;
import org.houzz.social.post.dto.LikeResponse;
import org.houzz.social.post.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{postId}/like")
public class LikeController {

    private final PostService postService;

    public LikeController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public LikeResponse toggle(@AuthenticationPrincipal AuthenticatedUser user,
                               @PathVariable Long postId) {
        return postService.toggleLike(postId, user.id());
    }
}
