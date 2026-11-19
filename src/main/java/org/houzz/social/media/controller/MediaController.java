package org.houzz.social.media.controller;

import jakarta.validation.Valid;
import org.houzz.social.auth.dto.AuthenticatedUser;
import org.houzz.social.media.dto.PresignRequest;
import org.houzz.social.media.dto.PresignResponse;
import org.houzz.social.media.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
public class MediaController {

    private final S3Service s3Service;

    public MediaController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/presign")
    @ResponseStatus(HttpStatus.CREATED)
    public PresignResponse presign(@AuthenticationPrincipal AuthenticatedUser user,
                                   @Valid @RequestBody PresignRequest request) {
        return s3Service.presignUpload(user.id(), request.contentType());
    }
}
