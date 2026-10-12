package br.com.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record OrderReviewRequest(
        @NotBlank String reviewId,
        @NotBlank String orderId,
        @NotNull Integer reviewScore,
        String reviewCommentTitle,
        String reviewCommentMessage,
        LocalDateTime reviewCreationDate,
        LocalDateTime reviewAnswerTimestamp
) {}
