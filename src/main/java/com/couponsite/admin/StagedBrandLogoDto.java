package com.couponsite.admin;

import java.time.LocalDateTime;

public record StagedBrandLogoDto(
    Long id,
    String storeName,
    String sourceSiteKey,
    String sourceUrl,
    String imageUrl,
    boolean posted,
    LocalDateTime postedAt,
    Long brandProfileId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
