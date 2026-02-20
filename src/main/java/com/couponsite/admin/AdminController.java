package com.couponsite.admin;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AppSettingService appSettingService;
    private final CrawlerLogService crawlerLogService;
    private final RetailMeNotCrawlerService retailMeNotCrawlerService;

    public AdminController(
        AppSettingService appSettingService,
        CrawlerLogService crawlerLogService,
        RetailMeNotCrawlerService retailMeNotCrawlerService
    ) {
        this.appSettingService = appSettingService;
        this.crawlerLogService = crawlerLogService;
        this.retailMeNotCrawlerService = retailMeNotCrawlerService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardDto dashboard() {
        return new AdminDashboardDto(appSettingService.isCrawlerEnabled(), crawlerLogService.latest());
    }

    @GetMapping("/settings")
    public CrawlerSettingsDto settings() {
        return new CrawlerSettingsDto(appSettingService.isCrawlerEnabled());
    }

    @PutMapping("/settings")
    public CrawlerSettingsDto updateSettings(@RequestBody CrawlerSettingsDto body) {
        return new CrawlerSettingsDto(appSettingService.setCrawlerEnabled(body.crawlerEnabled()));
    }

    @GetMapping("/logs")
    public List<CrawlerLogDto> logs() {
        return crawlerLogService.latest();
    }

    @PostMapping("/crawler/run")
    public ResponseEntity<String> runCrawler() {
        int count = retailMeNotCrawlerService.crawlLatest();
        return ResponseEntity.ok("Crawler done, upserts=" + count);
    }
}

