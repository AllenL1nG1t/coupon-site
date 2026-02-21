package com.couponsite.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentSettingsService {

    private static final String THEME_PRESET = "content.theme.preset";
    private static final String THEME_DISPLAY_NAME = "content.theme.displayName";
    private static final String SITE_NAME = "content.site.name";
    private static final String SITE_SLOGAN = "content.site.slogan";
    private static final String SITE_LOGO_TEXT = "content.site.logoText";
    private static final String SITE_LOGO_IMAGE_URL = "content.site.logoImageUrl";
    private static final String HERO_EYEBROW = "content.hero.eyebrow";
    private static final String HERO_TITLE = "content.hero.title";
    private static final String HERO_SUBTITLE = "content.hero.subtitle";
    private static final String HERO_BG_COLOR = "content.hero.bg.color";
    private static final String HERO_BG_IMAGE_URL = "content.hero.bg.imageUrl";
    private static final String FOOTER_TAGLINE = "content.footer.tagline";
    private static final String FOOTER_ABOUT_URL = "content.footer.aboutUrl";
    private static final String FOOTER_PRIVACY_URL = "content.footer.privacyUrl";
    private static final String FOOTER_CONTACT_URL = "content.footer.contactUrl";
    private static final String FOOTER_SUBMIT_COUPON_URL = "content.footer.submitCouponUrl";
    private static final String FOOTER_AFFILIATE_DISCLOSURE_URL = "content.footer.affiliateDisclosureUrl";
    private static final String FOOTER_TWITTER_URL = "content.footer.twitterUrl";
    private static final String FOOTER_INSTAGRAM_URL = "content.footer.instagramUrl";
    private static final String FOOTER_FACEBOOK_URL = "content.footer.facebookUrl";
    private static final String FOOTER_YOUTUBE_URL = "content.footer.youtubeUrl";

    private final AppSettingService appSettingService;

    public ContentSettingsService(AppSettingService appSettingService) {
        this.appSettingService = appSettingService;
    }

    public ContentSettingsDto get() {
        return new ContentSettingsDto(
            appSettingService.getString(THEME_PRESET, "scheme-a"),
            appSettingService.getString(THEME_DISPLAY_NAME, "Ocean Trust"),
            appSettingService.getString(SITE_NAME, "Dotiki Coupon"),
            appSettingService.getString(SITE_SLOGAN, "Smart Coupons. Real Savings."),
            appSettingService.getString(SITE_LOGO_TEXT, "D"),
            appSettingService.getString(SITE_LOGO_IMAGE_URL, ""),
            appSettingService.getString(HERO_EYEBROW, "SIMPLYCODES STYLE DEALS"),
            appSettingService.getString(HERO_TITLE, "Find verified promo codes that actually work"),
            appSettingService.getString(HERO_SUBTITLE, "Instant savings, clean checkout flow, and transparent code quality from real-time verification."),
            appSettingService.getString(HERO_BG_COLOR, "#f7f9fd"),
            appSettingService.getString(HERO_BG_IMAGE_URL, ""),
            appSettingService.getString(FOOTER_TAGLINE, "Deals are user-submitted and manually reviewed."),
            appSettingService.getString(FOOTER_ABOUT_URL, "/about.html"),
            appSettingService.getString(FOOTER_PRIVACY_URL, "/privacy.html"),
            appSettingService.getString(FOOTER_CONTACT_URL, "/contact.html"),
            appSettingService.getString(FOOTER_SUBMIT_COUPON_URL, "/submit-coupon.html"),
            appSettingService.getString(FOOTER_AFFILIATE_DISCLOSURE_URL, "/affiliate-disclosure.html"),
            appSettingService.getString(FOOTER_TWITTER_URL, "https://twitter.com/"),
            appSettingService.getString(FOOTER_INSTAGRAM_URL, "https://instagram.com/"),
            appSettingService.getString(FOOTER_FACEBOOK_URL, "https://facebook.com/"),
            appSettingService.getString(FOOTER_YOUTUBE_URL, "https://youtube.com/")
        );
    }

    @Transactional
    public ContentSettingsDto save(ContentSettingsDto request) {
        appSettingService.setString(THEME_PRESET, request.themePreset());
        appSettingService.setString(THEME_DISPLAY_NAME, request.themeDisplayName());
        appSettingService.setString(SITE_NAME, request.siteName());
        appSettingService.setString(SITE_SLOGAN, request.siteSlogan());
        appSettingService.setString(SITE_LOGO_TEXT, request.siteLogoText());
        appSettingService.setString(SITE_LOGO_IMAGE_URL, request.siteLogoImageUrl());
        appSettingService.setString(HERO_EYEBROW, request.heroEyebrow());
        appSettingService.setString(HERO_TITLE, request.heroTitle());
        appSettingService.setString(HERO_SUBTITLE, request.heroSubtitle());
        appSettingService.setString(HERO_BG_COLOR, request.heroBgColor());
        appSettingService.setString(HERO_BG_IMAGE_URL, request.heroBgImageUrl());
        appSettingService.setString(FOOTER_TAGLINE, request.footerTagline());
        appSettingService.setString(FOOTER_ABOUT_URL, request.footerAboutUrl());
        appSettingService.setString(FOOTER_PRIVACY_URL, request.footerPrivacyUrl());
        appSettingService.setString(FOOTER_CONTACT_URL, request.footerContactUrl());
        appSettingService.setString(FOOTER_SUBMIT_COUPON_URL, request.footerSubmitCouponUrl());
        appSettingService.setString(FOOTER_AFFILIATE_DISCLOSURE_URL, request.footerAffiliateDisclosureUrl());
        appSettingService.setString(FOOTER_TWITTER_URL, request.footerTwitterUrl());
        appSettingService.setString(FOOTER_INSTAGRAM_URL, request.footerInstagramUrl());
        appSettingService.setString(FOOTER_FACEBOOK_URL, request.footerFacebookUrl());
        appSettingService.setString(FOOTER_YOUTUBE_URL, request.footerYoutubeUrl());
        return get();
    }
}
