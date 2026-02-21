package com.couponsite.admin;

import java.time.LocalDateTime;

public record AdminCouponDto(
    Long id,
    String store,
    String title,
    String category,
    String expires,
    String couponCode,
    String affiliateUrl,
    String logoUrl,
    int clickCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
