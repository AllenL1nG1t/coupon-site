package com.couponsite.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdSettingsService {

    private static final String STRIP_ENABLED = "ads.strip.enabled";
    private static final String STRIP_TEXT = "ads.strip.text";
    private static final String STRIP_LINK = "ads.strip.link";

    private static final String HOME_TOP_ENABLED = "ads.home.top.enabled";
    private static final String HOME_MID_ENABLED = "ads.home.mid.enabled";
    private static final String HOME_SIDE_LEFT_ENABLED = "ads.home.side.left.enabled";
    private static final String HOME_SIDE_RIGHT_ENABLED = "ads.home.side.right.enabled";
    private static final String HOME_BOTTOM_ENABLED = "ads.home.bottom.enabled";

    private static final String BLOG_TOP_ENABLED = "ads.blog.top.enabled";
    private static final String BLOG_INLINE_ENABLED = "ads.blog.inline.enabled";
    private static final String BLOG_BOTTOM_ENABLED = "ads.blog.bottom.enabled";
    private static final String CODE_TOP_ENABLED = "ads.code.top.enabled";
    private static final String CODE_BOTTOM_ENABLED = "ads.code.bottom.enabled";

    private static final String ADSENSE_CLIENT_ID = "ads.adsense.clientId";
    private static final String ADSENSE_HOME_SLOT = "ads.adsense.home.slot";
    private static final String ADSENSE_BLOG_SLOT = "ads.adsense.blog.slot";
    private static final String ADSENSE_CODE_SLOT = "ads.adsense.code.slot";

    private final AppSettingService appSettingService;

    public AdSettingsService(AppSettingService appSettingService) {
        this.appSettingService = appSettingService;
    }

    public AdSettingsDto get() {
        return new AdSettingsDto(
            appSettingService.getBoolean(STRIP_ENABLED, true),
            appSettingService.getString(STRIP_TEXT, "🔥 Flash Sale: Save up to 70% today only"),
            appSettingService.getString(STRIP_LINK, "https://example-affiliate.com/flash"),
            appSettingService.getBoolean(HOME_TOP_ENABLED, true),
            appSettingService.getBoolean(HOME_MID_ENABLED, true),
            appSettingService.getBoolean(HOME_SIDE_LEFT_ENABLED, false),
            appSettingService.getBoolean(HOME_SIDE_RIGHT_ENABLED, false),
            appSettingService.getBoolean(HOME_BOTTOM_ENABLED, false),
            appSettingService.getBoolean(BLOG_TOP_ENABLED, false),
            appSettingService.getBoolean(BLOG_INLINE_ENABLED, true),
            appSettingService.getBoolean(BLOG_BOTTOM_ENABLED, false),
            appSettingService.getBoolean(CODE_TOP_ENABLED, false),
            appSettingService.getBoolean(CODE_BOTTOM_ENABLED, false),
            appSettingService.getString(ADSENSE_CLIENT_ID, ""),
            appSettingService.getString(ADSENSE_HOME_SLOT, ""),
            appSettingService.getString(ADSENSE_BLOG_SLOT, ""),
            appSettingService.getString(ADSENSE_CODE_SLOT, "")
        );
    }

    @Transactional
    public AdSettingsDto save(AdSettingsDto request) {
        appSettingService.setBoolean(STRIP_ENABLED, request.stripEnabled());
        appSettingService.setString(STRIP_TEXT, request.stripText());
        appSettingService.setString(STRIP_LINK, request.stripLink());

        appSettingService.setBoolean(HOME_TOP_ENABLED, request.homeTopEnabled());
        appSettingService.setBoolean(HOME_MID_ENABLED, request.homeMidEnabled());
        appSettingService.setBoolean(HOME_SIDE_LEFT_ENABLED, request.homeSideLeftEnabled());
        appSettingService.setBoolean(HOME_SIDE_RIGHT_ENABLED, request.homeSideRightEnabled());
        appSettingService.setBoolean(HOME_BOTTOM_ENABLED, request.homeBottomEnabled());

        appSettingService.setBoolean(BLOG_TOP_ENABLED, request.blogTopEnabled());
        appSettingService.setBoolean(BLOG_INLINE_ENABLED, request.blogInlineEnabled());
        appSettingService.setBoolean(BLOG_BOTTOM_ENABLED, request.blogBottomEnabled());
        appSettingService.setBoolean(CODE_TOP_ENABLED, request.codeTopEnabled());
        appSettingService.setBoolean(CODE_BOTTOM_ENABLED, request.codeBottomEnabled());

        appSettingService.setString(ADSENSE_CLIENT_ID, request.adsenseClientId());
        appSettingService.setString(ADSENSE_HOME_SLOT, request.homeAdsenseSlot());
        appSettingService.setString(ADSENSE_BLOG_SLOT, request.blogAdsenseSlot());
        appSettingService.setString(ADSENSE_CODE_SLOT, request.codeAdsenseSlot());

        return get();
    }
}
