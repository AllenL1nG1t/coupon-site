package com.couponsite.brand;

public record BrandProfileUpsertRequest(
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
    Boolean autoPostCoupons
) {
}
