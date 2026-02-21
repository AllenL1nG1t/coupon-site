package com.couponsite.admin;

import java.util.List;

public record AdminDashboardDto(
    boolean crawlerEnabled,
    String crawlerStatusText,
    List<CrawlerLogDto> logs,
    List<BrandCouponStatDto> brandCouponStats
) {
}

