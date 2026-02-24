package com.couponsite.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AppSettingService {

    private static final String COUPON_CRAWLER_ENABLED_KEY = "crawler.coupon.enabled";
    private static final String BRAND_CRAWLER_ENABLED_KEY = "crawler.brand.enabled";
    private static final String BRAND_LOGO_CRAWLER_ENABLED_KEY = "crawler.brand-logo.enabled";
    private static final String COUPON_CRAWLER_INTERVAL_MS_KEY = "crawler.coupon.interval-ms";
    private static final String BRAND_CRAWLER_INTERVAL_MS_KEY = "crawler.brand.interval-ms";
    private static final String BRAND_LOGO_CRAWLER_INTERVAL_MS_KEY = "crawler.brand-logo.interval-ms";
    private static final String COUPON_CRAWLER_RUN_AT_KEY = "crawler.coupon.run-at";
    private static final String BRAND_CRAWLER_RUN_AT_KEY = "crawler.brand.run-at";
    private static final String BRAND_LOGO_CRAWLER_RUN_AT_KEY = "crawler.brand-logo.run-at";
    private static final String COUPON_CRAWLER_LAST_RUN_AT_KEY = "crawler.coupon.last-run-at";
    private static final String BRAND_CRAWLER_LAST_RUN_AT_KEY = "crawler.brand.last-run-at";
    private static final String BRAND_LOGO_CRAWLER_LAST_RUN_AT_KEY = "crawler.brand-logo.last-run-at";
    private static final String LEGACY_CRAWLER_ENABLED_KEY = "crawler.enabled";
    private static final String LEGACY_CRAWLER_INTERVAL_MS_KEY = "crawler.interval-ms";
    private static final long MIN_CRAWLER_INTERVAL_MS = 60_000L;
    private static final long MAX_CRAWLER_INTERVAL_MS = 86_400_000L;

    private final AppSettingRepository appSettingRepository;
    private final long defaultCrawlerIntervalMs;

    public AppSettingService(
        AppSettingRepository appSettingRepository,
        @Value("${crawler.fixed-delay-ms:1800000}") long defaultCrawlerIntervalMs
    ) {
        this.appSettingRepository = appSettingRepository;
        this.defaultCrawlerIntervalMs = clampCrawlerInterval(defaultCrawlerIntervalMs);
    }

    public boolean isCouponCrawlerEnabled() {
        boolean legacy = getBoolean(LEGACY_CRAWLER_ENABLED_KEY, false);
        return getBoolean(COUPON_CRAWLER_ENABLED_KEY, legacy);
    }

    public boolean isBrandCrawlerEnabled() {
        return getBoolean(BRAND_CRAWLER_ENABLED_KEY, false);
    }

    public boolean isBrandLogoCrawlerEnabled() {
        return getBoolean(BRAND_LOGO_CRAWLER_ENABLED_KEY, false);
    }

    public long getCouponCrawlerIntervalMs() {
        long legacy = getLong(LEGACY_CRAWLER_INTERVAL_MS_KEY, defaultCrawlerIntervalMs);
        long raw = getLong(COUPON_CRAWLER_INTERVAL_MS_KEY, legacy);
        return clampCrawlerInterval(raw);
    }

    public long getBrandCrawlerIntervalMs() {
        long raw = getLong(BRAND_CRAWLER_INTERVAL_MS_KEY, defaultCrawlerIntervalMs);
        return clampCrawlerInterval(raw);
    }

    public long getBrandLogoCrawlerIntervalMs() {
        long raw = getLong(BRAND_LOGO_CRAWLER_INTERVAL_MS_KEY, defaultCrawlerIntervalMs);
        return clampCrawlerInterval(raw);
    }

    public String getCouponCrawlerRunAt() {
        return normalizeRunAt(getString(COUPON_CRAWLER_RUN_AT_KEY, "00:00"));
    }

    public String getBrandCrawlerRunAt() {
        return normalizeRunAt(getString(BRAND_CRAWLER_RUN_AT_KEY, "00:00"));
    }

    public String getBrandLogoCrawlerRunAt() {
        return normalizeRunAt(getString(BRAND_LOGO_CRAWLER_RUN_AT_KEY, "00:00"));
    }

    public String getCouponCrawlerLastRunAt() {
        return normalizeDateTime(getString(COUPON_CRAWLER_LAST_RUN_AT_KEY, ""));
    }

    public String getBrandCrawlerLastRunAt() {
        return normalizeDateTime(getString(BRAND_CRAWLER_LAST_RUN_AT_KEY, ""));
    }

    public String getBrandLogoCrawlerLastRunAt() {
        return normalizeDateTime(getString(BRAND_LOGO_CRAWLER_LAST_RUN_AT_KEY, ""));
    }

    @Transactional
    public boolean setCouponCrawlerEnabled(boolean enabled) {
        setBoolean(COUPON_CRAWLER_ENABLED_KEY, enabled);
        return enabled;
    }

    @Transactional
    public boolean setBrandCrawlerEnabled(boolean enabled) {
        setBoolean(BRAND_CRAWLER_ENABLED_KEY, enabled);
        return enabled;
    }

    @Transactional
    public boolean setBrandLogoCrawlerEnabled(boolean enabled) {
        setBoolean(BRAND_LOGO_CRAWLER_ENABLED_KEY, enabled);
        return enabled;
    }

    @Transactional
    public long setCouponCrawlerIntervalMs(long intervalMs) {
        long normalized = clampCrawlerInterval(intervalMs);
        setLong(COUPON_CRAWLER_INTERVAL_MS_KEY, normalized);
        return normalized;
    }

    @Transactional
    public long setBrandCrawlerIntervalMs(long intervalMs) {
        long normalized = clampCrawlerInterval(intervalMs);
        setLong(BRAND_CRAWLER_INTERVAL_MS_KEY, normalized);
        return normalized;
    }

    @Transactional
    public long setBrandLogoCrawlerIntervalMs(long intervalMs) {
        long normalized = clampCrawlerInterval(intervalMs);
        setLong(BRAND_LOGO_CRAWLER_INTERVAL_MS_KEY, normalized);
        return normalized;
    }

    @Transactional
    public String setCouponCrawlerRunAt(String runAt) {
        String normalized = normalizeRunAt(runAt);
        setString(COUPON_CRAWLER_RUN_AT_KEY, normalized);
        return normalized;
    }

    @Transactional
    public String setBrandCrawlerRunAt(String runAt) {
        String normalized = normalizeRunAt(runAt);
        setString(BRAND_CRAWLER_RUN_AT_KEY, normalized);
        return normalized;
    }

    @Transactional
    public String setBrandLogoCrawlerRunAt(String runAt) {
        String normalized = normalizeRunAt(runAt);
        setString(BRAND_LOGO_CRAWLER_RUN_AT_KEY, normalized);
        return normalized;
    }

    @Transactional
    public void markCouponCrawlerLastRunNow() {
        setString(COUPON_CRAWLER_LAST_RUN_AT_KEY, LocalDateTime.now().toString());
    }

    @Transactional
    public void markBrandCrawlerLastRunNow() {
        setString(BRAND_CRAWLER_LAST_RUN_AT_KEY, LocalDateTime.now().toString());
    }

    @Transactional
    public void markBrandLogoCrawlerLastRunNow() {
        setString(BRAND_LOGO_CRAWLER_LAST_RUN_AT_KEY, LocalDateTime.now().toString());
    }

    public boolean isRunWindowOpen(String runAt, String lastRunAt) {
        LocalTime runTime = parseRunAt(runAt);
        if (runTime == null) {
            return true;
        }
        LocalDateTime lastRun = parseDateTime(lastRunAt);
        if (lastRun != null && !lastRun.toLocalDate().isBefore(LocalDate.now())) {
            return true;
        }
        return !LocalTime.now().isBefore(runTime);
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

    public long getLong(String key, long defaultValue) {
        return appSettingRepository.findById(key)
            .map(AppSetting::getSettingValue)
            .map(value -> {
                try {
                    return Long.parseLong(value.trim());
                } catch (NumberFormatException ex) {
                    return defaultValue;
                }
            })
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

    @Transactional
    public void setLong(String key, long value) {
        setString(key, Long.toString(value));
    }

    private long clampCrawlerInterval(long intervalMs) {
        if (intervalMs < MIN_CRAWLER_INTERVAL_MS) {
            return MIN_CRAWLER_INTERVAL_MS;
        }
        if (intervalMs > MAX_CRAWLER_INTERVAL_MS) {
            return MAX_CRAWLER_INTERVAL_MS;
        }
        return intervalMs;
    }

    private String normalizeRunAt(String raw) {
        LocalTime parsed = parseRunAt(raw);
        return parsed == null ? "00:00" : parsed.toString();
    }

    private LocalTime parseRunAt(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(value.length() == 5 ? value + ":00" : value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private String normalizeDateTime(String raw) {
        LocalDateTime parsed = parseDateTime(raw);
        return parsed == null ? "" : parsed.toString();
    }

    private LocalDateTime parseDateTime(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}

