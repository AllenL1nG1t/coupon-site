package com.couponsite.admin;

import java.util.List;

public record AdminDashboardDto(boolean crawlerEnabled, List<CrawlerLogDto> logs) {
}

