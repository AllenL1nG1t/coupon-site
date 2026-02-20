package com.couponsite.admin;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CrawlerLogService {

    private final CrawlerLogRepository crawlerLogRepository;

    public CrawlerLogService(CrawlerLogRepository crawlerLogRepository) {
        this.crawlerLogRepository = crawlerLogRepository;
    }

    public void info(String message) {
        save("INFO", message);
    }

    public void warn(String message) {
        save("WARN", message);
    }

    public void error(String message) {
        save("ERROR", message);
    }

    public List<CrawlerLogDto> latest() {
        return crawlerLogRepository.findTop200ByOrderByCreatedAtDesc()
            .stream()
            .map(log -> new CrawlerLogDto(log.getId(), log.getLevel(), log.getMessage(), log.getCreatedAt()))
            .toList();
    }

    private void save(String level, String message) {
        CrawlerLog log = new CrawlerLog();
        log.setLevel(level);
        log.setMessage(message);
        crawlerLogRepository.save(log);
    }
}

