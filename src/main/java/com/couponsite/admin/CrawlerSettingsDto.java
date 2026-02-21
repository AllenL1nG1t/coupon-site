package com.couponsite.admin;

public record CrawlerSettingsDto(
    boolean crawlerEnabled,
    boolean brandCrawlerEnabled,
    long crawlerIntervalMinutes
) {
}

