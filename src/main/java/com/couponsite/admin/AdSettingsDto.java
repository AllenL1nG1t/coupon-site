package com.couponsite.admin;

public record AdSettingsDto(
    boolean stripEnabled,
    String stripText,
    String stripLink,
    boolean homeTopEnabled,
    boolean homeMidEnabled,
    boolean homeSideLeftEnabled,
    boolean homeSideRightEnabled,
    boolean homeBottomEnabled,
    boolean blogTopEnabled,
    boolean blogInlineEnabled,
    boolean blogBottomEnabled,
    boolean codeTopEnabled,
    boolean codeBottomEnabled,
    String adsenseClientId,
    String homeAdsenseSlot,
    String blogAdsenseSlot,
    String codeAdsenseSlot
) {
}
