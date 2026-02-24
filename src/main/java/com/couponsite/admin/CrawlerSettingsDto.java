package com.couponsite.admin;

public record CrawlerSettingsDto(
    boolean couponCrawlerEnabled,
    boolean brandCrawlerEnabled,
    boolean brandLogoCrawlerEnabled,
    long couponCrawlerIntervalMinutes,
    long brandCrawlerIntervalMinutes,
    long brandLogoCrawlerIntervalMinutes,
    String couponCrawlerRunAt,
    String brandCrawlerRunAt,
    String brandLogoCrawlerRunAt,
    String couponCrawlerLastRunAt,
    String brandCrawlerLastRunAt,
    String brandLogoCrawlerLastRunAt
) {
}

