package com.couponsite.admin;

import java.time.LocalDateTime;

public record CrawlerSiteDto(
    Long id,
    String siteKey,
    String siteName,
    String baseUrl,
    boolean active,
    boolean couponEnabled,
    boolean brandEnabled,
    boolean logoEnabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

