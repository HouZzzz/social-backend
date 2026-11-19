package org.houzz.social.post.controller;

import jakarta.validation.Valid;
import org.houzz.social.auth.dto.AuthenticatedUser;
import org.houzz.social.post.dto.CreatePostRequest;
import org.houzz.social.post.dto.PostDto;
import org.houzz.social.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostDto> feed(@AuthenticationPrincipal AuthenticatedUser user,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false, defaultValue = "all") String filter,
                              @RequestParam(required = false, defaultValue = "50") int limit,
                              @RequestParam(required = false, defaultValue = "0") int offset) {
        return postService.feed(user.id(), search, filter, limit, offset);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto create(@AuthenticationPrincipal AuthenticatedUser user,
                          @Valid @RequestBody CreatePostRequest request) {
        return postService.create(user.id(), request);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser user,
                       @PathVariable Long postId) {
        postService.delete(postId, user.id());
    }
}
