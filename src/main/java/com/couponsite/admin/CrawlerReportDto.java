package com.couponsite.admin;

import java.util.List;

public record CrawlerReportDto(
    long totalStagedCoupons,
    long totalBrands,
    long totalStagedLogos,
    List<CrawlerSiteReportDto> sites
) {
}
