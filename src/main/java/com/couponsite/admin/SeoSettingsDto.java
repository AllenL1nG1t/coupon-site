package com.couponsite.admin;

public record SeoSettingsDto(
    String title,
    String description,
    String keywords,
    String ogImageUrl,
    String canonicalBaseUrl
) {
}
