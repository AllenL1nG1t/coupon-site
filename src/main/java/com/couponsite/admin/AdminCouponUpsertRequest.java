package com.couponsite.admin;

public record AdminCouponUpsertRequest(
    Long id,
    String store,
    String title,
    String category,
    String expires,
    String couponCode,
    String affiliateUrl,
    String logoUrl,
    String source
) {
}
