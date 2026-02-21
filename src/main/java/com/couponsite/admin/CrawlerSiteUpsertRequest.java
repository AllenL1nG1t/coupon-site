package com.couponsite.admin;

public record CrawlerSiteUpsertRequest(
    Long id,
    String siteName,
    String baseUrl,
    Boolean active,
    Boolean couponEnabled,
    Boolean brandEnabled,
    Boolean logoEnabled
) {
}

