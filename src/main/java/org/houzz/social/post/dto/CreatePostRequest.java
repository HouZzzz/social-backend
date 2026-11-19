package org.houzz.social.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "Текст поста не может быть пустым")
        @Size(max = 280, message = "Текст поста не может превышать 280 символов")
        String content,

        // nullable
        @Size(max = 255, message = "Слишком длинный ключ изображения")
        String imageKey
) {
}
