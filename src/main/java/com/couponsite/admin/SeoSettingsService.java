package com.couponsite.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeoSettingsService {

    private static final String SEO_TITLE = "seo.title";
    private static final String SEO_DESCRIPTION = "seo.description";
    private static final String SEO_KEYWORDS = "seo.keywords";
    private static final String SEO_OG_IMAGE = "seo.og.imageUrl";
    private static final String SEO_CANONICAL_BASE = "seo.canonical.baseUrl";

    private final AppSettingService appSettingService;

    public SeoSettingsService(AppSettingService appSettingService) {
        this.appSettingService = appSettingService;
    }

    public SeoSettingsDto get() {
        return new SeoSettingsDto(
            appSettingService.getString(SEO_TITLE, "Dotiki Coupon - Verified Coupons & Real Savings"),
            appSettingService.getString(SEO_DESCRIPTION, "Find verified promo codes, active deals, and cashback guides across top stores."),
            appSettingService.getString(SEO_KEYWORDS, "coupon, promo code, deals, discounts, cashback"),
            appSettingService.getString(SEO_OG_IMAGE, ""),
            appSettingService.getString(SEO_CANONICAL_BASE, "")
        );
    }

    @Transactional
    public SeoSettingsDto save(SeoSettingsDto request) {
        appSettingService.setString(SEO_TITLE, request.title());
        appSettingService.setString(SEO_DESCRIPTION, request.description());
        appSettingService.setString(SEO_KEYWORDS, request.keywords());
        appSettingService.setString(SEO_OG_IMAGE, request.ogImageUrl());
        appSettingService.setString(SEO_CANONICAL_BASE, request.canonicalBaseUrl());
        return get();
    }
}
