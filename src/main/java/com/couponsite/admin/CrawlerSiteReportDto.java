package com.couponsite.admin;

public record CrawlerSiteReportDto(
    String siteKey,
    String siteName,
    long stagedCouponCount,
    long brandCount,
    long logoCount
) {
}
