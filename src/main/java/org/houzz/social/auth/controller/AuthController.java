package org.houzz.social.auth.controller;

import jakarta.validation.Valid;
import org.houzz.social.auth.dto.AuthResponse;
import org.houzz.social.auth.dto.AuthenticatedUser;
import org.houzz.social.auth.dto.LoginRequest;
import org.houzz.social.auth.dto.RegisterRequest;
import org.houzz.social.auth.dto.UserDto;
import org.houzz.social.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal AuthenticatedUser user) {
        return authService.currentUser(user.id());
    }
}
