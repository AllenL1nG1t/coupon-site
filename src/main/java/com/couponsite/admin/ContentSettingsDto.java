package com.couponsite.admin;

public record ContentSettingsDto(
    String themePreset,
    String heroEyebrow,
    String heroTitle,
    String heroSubtitle,
    String heroBgColor,
    String heroBgImageUrl,
    String footerTagline,
    String footerAboutUrl,
    String footerPrivacyUrl,
    String footerContactUrl,
    String footerSubmitCouponUrl,
    String footerAffiliateDisclosureUrl,
    String footerTwitterUrl,
    String footerInstagramUrl,
    String footerFacebookUrl,
    String footerYoutubeUrl
) {
}
