package com.couponsite.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentSettingsService {

    private static final String HERO_EYEBROW = "content.hero.eyebrow";
    private static final String HERO_TITLE = "content.hero.title";
    private static final String HERO_SUBTITLE = "content.hero.subtitle";

    private final AppSettingService appSettingService;

    public ContentSettingsService(AppSettingService appSettingService) {
        this.appSettingService = appSettingService;
    }

    public ContentSettingsDto get() {
        return new ContentSettingsDto(
            appSettingService.getString(HERO_EYEBROW, "SIMPLYCODES STYLE DEALS"),
            appSettingService.getString(HERO_TITLE, "Find verified promo codes that actually work"),
            appSettingService.getString(HERO_SUBTITLE, "Instant savings, clean checkout flow, and transparent code quality from real-time verification.")
        );
    }

    @Transactional
    public ContentSettingsDto save(ContentSettingsDto request) {
        appSettingService.setString(HERO_EYEBROW, request.heroEyebrow());
        appSettingService.setString(HERO_TITLE, request.heroTitle());
        appSettingService.setString(HERO_SUBTITLE, request.heroSubtitle());
        return get();
    }
}
