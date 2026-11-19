package org.houzz.social.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Комментарий не может быть пустым")
        @Size(max = 500, message = "Комментарий не может превышать 500 символов")
        String text
) {
}
