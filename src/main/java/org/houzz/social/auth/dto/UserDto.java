package org.houzz.social.auth.dto;

import org.houzz.social.auth.entity.User;

public record UserDto(Long id, String username) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }
}
