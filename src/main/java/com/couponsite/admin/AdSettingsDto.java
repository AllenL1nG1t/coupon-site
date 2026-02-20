package com.couponsite.admin;

public record AdSettingsDto(
    boolean stripEnabled,
    String stripText,
    String stripLink,
    boolean homeTopEnabled,
    boolean homeMidEnabled,
    boolean homeBottomEnabled,
    boolean blogTopEnabled,
    boolean blogInlineEnabled,
    boolean blogBottomEnabled,
    String adsenseClientId,
    String homeAdsenseSlot,
    String blogAdsenseSlot
) {
}
