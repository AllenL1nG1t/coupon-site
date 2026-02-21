package com.couponsite.blog;

import java.time.LocalDateTime;

public record BlogPostDto(
    Long id,
    String title,
    String summary,
    String content,
    String coverImageUrl,
    boolean published,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
