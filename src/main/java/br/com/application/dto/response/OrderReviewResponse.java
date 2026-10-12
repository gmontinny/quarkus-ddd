package br.com.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record OrderReviewResponse(
        String reviewId,
        String orderId,
        Integer reviewScore,
        String reviewCommentTitle,
        String reviewCommentMessage,
        LocalDateTime reviewCreationDate,
        LocalDateTime reviewAnswerTimestamp,
        List<Link> links
) {}
