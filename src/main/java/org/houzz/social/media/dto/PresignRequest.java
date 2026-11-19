package org.houzz.social.media.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PresignRequest(
        @NotBlank(message = "contentType обязателен")
        @Pattern(regexp = "^image/(jpeg|jpg|png|webp)$",
                message = "Поддерживаются только image/jpeg, image/png и image/webp")
        String contentType
) {
}
