package org.houzz.social.auth.dto;

import org.houzz.social.auth.entity.User;

public record AuthResponse(String token, UserDto user) {

    public static AuthResponse of(String token, User user) {
        return new AuthResponse(token, UserDto.from(user));
    }
}
