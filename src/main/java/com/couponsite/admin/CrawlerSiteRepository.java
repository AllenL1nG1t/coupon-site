package com.couponsite.admin;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawlerSiteRepository extends JpaRepository<CrawlerSite, Long> {
    Optional<CrawlerSite> findBySiteKeyIgnoreCase(String siteKey);
    List<CrawlerSite> findAllByOrderByIdAsc();
}

