package com.couponsite.admin;

public record CrawlerSettingsDto(
    boolean couponCrawlerEnabled,
    boolean brandCrawlerEnabled,
    boolean brandLogoCrawlerEnabled,
    long couponCrawlerIntervalMinutes,
    long brandCrawlerIntervalMinutes,
    long brandLogoCrawlerIntervalMinutes
) {
}

