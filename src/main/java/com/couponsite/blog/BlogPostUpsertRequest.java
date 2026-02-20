package com.couponsite.blog;

public record BlogPostUpsertRequest(
    Long id,
    String title,
    String summary,
    String content,
    String coverImageUrl,
    boolean published
) {
}
