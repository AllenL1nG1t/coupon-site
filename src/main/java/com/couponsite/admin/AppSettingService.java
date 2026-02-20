package com.couponsite.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppSettingService {

    private static final String CRAWLER_ENABLED_KEY = "crawler.enabled";

    private final AppSettingRepository appSettingRepository;

    public AppSettingService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    public boolean isCrawlerEnabled() {
        return appSettingRepository.findById(CRAWLER_ENABLED_KEY)
            .map(AppSetting::getSettingValue)
            .map(Boolean::parseBoolean)
            .orElse(false);
    }

    @Transactional
    public boolean setCrawlerEnabled(boolean enabled) {
        AppSetting setting = appSettingRepository.findById(CRAWLER_ENABLED_KEY).orElseGet(AppSetting::new);
        setting.setSettingKey(CRAWLER_ENABLED_KEY);
        setting.setSettingValue(Boolean.toString(enabled));
        appSettingRepository.save(setting);
        return enabled;
    }
}

