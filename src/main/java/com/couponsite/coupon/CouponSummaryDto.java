package com.couponsite.coupon;

public record CouponSummaryDto(
    Long id,
    String store,
    String title,
    String category,
    String expires,
    boolean expired,
    String logoUrl,
    int clickCount
) {
}

