package com.couponsite.admin;

import java.time.LocalDateTime;

public record AdminStagedCouponDto(
    Long id,
    String store,
    String title,
    String category,
    String expires,
    String couponCode,
    String affiliateUrl,
    String logoUrl,
    String source,
    boolean posted,
    LocalDateTime postedAt,
    Long postedCouponId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
