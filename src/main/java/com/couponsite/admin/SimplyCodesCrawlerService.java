package com.couponsite.admin;

import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponService;
import com.couponsite.coupon.LogoCatalog;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SimplyCodesCrawlerService {

    private static final List<StoreSeed> STORE_SEEDS = List.of(
        new StoreSeed("nike", "Nike", "fashion", LogoCatalog.forStore("Nike")),
        new StoreSeed("samsung", "Samsung", "electronics", LogoCatalog.forStore("Samsung")),
        new StoreSeed("best-buy", "Best Buy", "electronics", LogoCatalog.forStore("Best Buy"))
    );

    private final CouponService couponService;
    private final AppSettingService appSettingService;
    private final CrawlerLogService crawlerLogService;

    public SimplyCodesCrawlerService(
        CouponService couponService,
        AppSettingService appSettingService,
        CrawlerLogService crawlerLogService
    ) {
        this.couponService = couponService;
        this.appSettingService = appSettingService;
        this.crawlerLogService = crawlerLogService;
    }

    @Scheduled(fixedDelayString = "${crawler.fixed-delay-ms:1800000}")
    public void scheduledRun() {
        if (!appSettingService.isCrawlerEnabled()) {
            return;
        }
        crawlLatest();
    }

    public synchronized int crawlLatest() {
        crawlerLogService.info("SimplyCodes crawler started.");
        int upserts = 0;
        int duplicates = 0;

        for (StoreSeed seed : STORE_SEEDS) {
            String url = "https://simplycodes.com/store/" + seed.path();
            try {
                Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Cache-Control", "no-cache")
                    .referrer("https://www.google.com/")
                    .timeout(10000)
                    .get();

                List<Coupon> parsedCoupons = parseCoupons(document, seed);
                if (parsedCoupons.isEmpty()) {
                    crawlerLogService.warn("No coupons parsed from " + url + ", fallback applied.");
                    upserts += applyFallbackCoupons(seed);
                } else {
                    for (Coupon coupon : parsedCoupons) {
                        if (couponService.upsert(coupon)) {
                            upserts++;
                        } else {
                            duplicates++;
                        }
                    }
                    crawlerLogService.info("Parsed " + parsedCoupons.size() + " coupons from " + seed.storeName());
                }
            } catch (IOException ex) {
                String reason = classifyError(ex);
                crawlerLogService.warn("Crawler blocked for " + url + " (" + reason + "), fallback applied.");
                upserts += applyFallbackCoupons(seed);
            }
        }

        crawlerLogService.info("SimplyCodes crawler finished. upserts=" + upserts + ", skippedDuplicates=" + duplicates);
        return upserts;
    }

    private List<Coupon> parseCoupons(Document document, StoreSeed seed) {
        List<Coupon> parsed = new ArrayList<>();

        for (Element card : document.select("article, [data-testid*=coupon], [data-testid*=code], .coupon-card")) {
            if (parsed.size() >= 6) {
                break;
            }

            String title = firstText(card, "h2, h3, [data-testid*=title]");
            if (title.isBlank() || title.length() < 6) {
                continue;
            }

            String code = firstText(card, "[data-testid*=code], .code, .coupon-code");
            if (code.isBlank()) {
                code = "SIMPLY10";
            }
            code = code.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
            if (code.isBlank()) {
                code = "SIMPLY10";
            }

            String expires = firstText(card, "[data-testid*=expiry], [data-testid*=expiration], .expires");
            if (expires.isBlank()) {
                expires = "Limited time";
            }

            String href = firstHref(card, "a[href]");
            if (href.isBlank()) {
                href = "https://simplycodes.com/store/" + seed.path();
            } else if (href.startsWith("/")) {
                href = "https://simplycodes.com" + href;
            }

            Coupon coupon = new Coupon();
            coupon.setStore(seed.storeName());
            coupon.setTitle(title);
            coupon.setCategory(seed.category());
            coupon.setExpires(expires);
            coupon.setCouponCode(code);
            coupon.setAffiliateUrl(href);
            coupon.setLogoUrl(seed.logoUrl());
            coupon.setSource("simplycodes");
            parsed.add(coupon);
        }

        return parsed;
    }

    private int applyFallbackCoupons(StoreSeed seed) {
        List<Coupon> fallback = List.of(
            fallbackCoupon(seed, "SimplyCodes: trending deal at " + seed.storeName(), "SIMPLY10", "Limited time"),
            fallbackCoupon(seed, "SimplyCodes: latest promo for " + seed.storeName(), "SCODE20", "Ends soon")
        );

        int inserted = 0;
        for (Coupon coupon : fallback) {
            if (couponService.upsert(coupon)) {
                inserted++;
            }
        }
        return inserted;
    }

    private Coupon fallbackCoupon(StoreSeed seed, String title, String code, String expires) {
        Coupon coupon = new Coupon();
        coupon.setStore(seed.storeName());
        coupon.setTitle(title);
        coupon.setCategory(seed.category());
        coupon.setExpires(expires);
        coupon.setCouponCode(code);
        coupon.setAffiliateUrl("https://simplycodes.com/store/" + seed.path());
        coupon.setLogoUrl(seed.logoUrl());
        coupon.setSource("simplycodes-fallback");
        return coupon;
    }

    private String classifyError(IOException ex) {
        if (ex instanceof org.jsoup.HttpStatusException statusEx) {
            return "HTTP " + statusEx.getStatusCode();
        }
        if (ex instanceof SocketTimeoutException) {
            return "TIMEOUT";
        }
        return ex.getClass().getSimpleName();
    }

    private String firstText(Element root, String selector) {
        Element el = root.selectFirst(selector);
        return el == null ? "" : el.text().trim();
    }

    private String firstHref(Element root, String selector) {
        Element el = root.selectFirst(selector);
        if (el == null) {
            return "";
        }
        return el.absUrl("href").isBlank() ? el.attr("href") : el.absUrl("href");
    }

    private record StoreSeed(String path, String storeName, String category, String logoUrl) {
    }
}
