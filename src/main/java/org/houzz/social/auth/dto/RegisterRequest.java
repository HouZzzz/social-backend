package org.houzz.social.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(min = 3, max = 32, message = "Имя пользователя должно быть от 3 до 32 символов")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$",
                message = "Имя пользователя может содержать только латиницу, цифры и подчёркивание")
        String username,

        @NotBlank(message = "Пароль обязателен")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
                message = "Пароль должен содержать минимум 8 символов, включая буквы и цифры")
        String password
) {
}
