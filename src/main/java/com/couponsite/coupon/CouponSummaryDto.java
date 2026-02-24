package com.couponsite.coupon;

import java.time.LocalDateTime;

public record CouponSummaryDto(
    Long id,
    String store,
    String title,
    String category,
    String expires,
    boolean expired,
    String logoUrl,
    int clickCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

