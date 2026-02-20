package com.couponsite.admin;

import java.time.LocalDateTime;

public record CrawlerLogDto(Long id, String level, String message, LocalDateTime createdAt) {
}

