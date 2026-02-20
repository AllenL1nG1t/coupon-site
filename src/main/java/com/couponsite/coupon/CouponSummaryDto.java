package com.couponsite.coupon;

public record CouponSummaryDto(
    Long id,
    String store,
    String title,
    String category,
    String expires,
    String logoUrl,
    String source,
    int clickCount
) {
}

