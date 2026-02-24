package com.couponsite.brand;

import java.time.LocalDateTime;

public record BrandProfileDto(
    Long id,
    String slug,
    String storeName,
    String title,
    String summary,
    String description,
    String heroImageUrl,
    String logoUrl,
    String officialUrl,
    String affiliateUrl,
    boolean autoPostCoupons,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
