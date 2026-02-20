package com.couponsite.admin;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawlerLogRepository extends JpaRepository<CrawlerLog, Long> {
    List<CrawlerLog> findTop200ByOrderByCreatedAtDesc();
}

