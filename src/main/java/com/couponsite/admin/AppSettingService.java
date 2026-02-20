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
        return getBoolean(CRAWLER_ENABLED_KEY, false);
    }

    @Transactional
    public boolean setCrawlerEnabled(boolean enabled) {
        setBoolean(CRAWLER_ENABLED_KEY, enabled);
        return enabled;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return appSettingRepository.findById(key)
            .map(AppSetting::getSettingValue)
            .map(Boolean::parseBoolean)
            .orElse(defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return appSettingRepository.findById(key)
            .map(AppSetting::getSettingValue)
            .orElse(defaultValue);
    }

    @Transactional
    public void setBoolean(String key, boolean value) {
        setString(key, Boolean.toString(value));
    }

    @Transactional
    public void setString(String key, String value) {
        AppSetting setting = appSettingRepository.findById(key).orElseGet(AppSetting::new);
        setting.setSettingKey(key);
        setting.setSettingValue(value == null ? "" : value);
        appSettingRepository.save(setting);
    }
}

