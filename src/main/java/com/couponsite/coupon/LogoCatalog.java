package com.couponsite.coupon;

import java.util.Locale;
import java.util.Map;

public final class LogoCatalog {

    private static final Map<String, String> STORE_LOGOS = Map.of(
        "nike", "/logos/nike.svg",
        "expedia", "/logos/expedia.svg",
        "best buy", "/logos/bestbuy.svg",
        "doordash", "/logos/doordash.svg",
        "macy's", "/logos/macys.svg",
        "samsung", "/logos/samsung.svg"
    );

    private LogoCatalog() {
    }

    public static String forStore(String store) {
        if (store == null || store.isBlank()) {
            return "/logos/default.svg";
        }
        return STORE_LOGOS.getOrDefault(store.trim().toLowerCase(Locale.ROOT), "/logos/default.svg");
    }
}

