package com.couponsite.admin;

import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponService;
import com.couponsite.coupon.LogoCatalog;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RetailMeNotCrawlerService {

    private static final List<StoreSeed> STORE_SEEDS = List.of(
        new StoreSeed("nike.com", "Nike", "fashion", LogoCatalog.forStore("Nike")),
        new StoreSeed("samsung.com", "Samsung", "electronics", LogoCatalog.forStore("Samsung")),
        new StoreSeed("bestbuy.com", "Best Buy", "electronics", LogoCatalog.forStore("Best Buy"))
    );

    private final CouponService couponService;
    private final AppSettingService appSettingService;
    private final CrawlerLogService crawlerLogService;
    private final AtomicLong lastScheduledRunAt = new AtomicLong(0L);

    public RetailMeNotCrawlerService(
        CouponService couponService,
        AppSettingService appSettingService,
        CrawlerLogService crawlerLogService
    ) {
        this.couponService = couponService;
        this.appSettingService = appSettingService;
        this.crawlerLogService = crawlerLogService;
    }

    @Scheduled(fixedDelay = 30_000)
    public void scheduledRun() {
        if (!appSettingService.isCrawlerEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        long intervalMs = appSettingService.getCrawlerIntervalMs();
        long lastRun = lastScheduledRunAt.get();
        if (now - lastRun < intervalMs) {
            return;
        }
        if (!lastScheduledRunAt.compareAndSet(lastRun, now)) {
            return;
        }
        crawlLatest();
    }

    public synchronized int crawlLatest() {
        crawlerLogService.info("RetailMeNot crawler started.");
        int upserts = 0;
        int duplicates = 0;

        for (StoreSeed seed : STORE_SEEDS) {
            String url = "https://www.retailmenot.com/view/" + seed.path();
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

        crawlerLogService.info("RetailMeNot crawler finished. upserts=" + upserts + ", skippedDuplicates=" + duplicates);
        return upserts;
    }

    private List<Coupon> parseCoupons(Document document, StoreSeed seed) {
        List<Coupon> parsed = new ArrayList<>();

        for (Element card : document.select("article, section[data-testid*=offer], div[data-testid*=offer]")) {
            if (parsed.size() >= 6) {
                break;
            }

            String title = firstText(card, "h2, h3, [data-testid*=title]");
            if (title.isBlank() || title.length() < 6) {
                continue;
            }

            String code = firstText(card, "[data-testid*=code], .offer-code, .code");
            if (code.isBlank()) {
                code = "SEEDEAL";
            }
            code = code.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
            if (code.isBlank()) {
                code = "SEEDEAL";
            }

            String expires = firstText(card, "[data-testid*=expiration], .expiration, .expires");
            if (expires.isBlank()) {
                expires = "Limited time";
            }

            String href = firstHref(card, "a[href]");
            if (href.isBlank()) {
                href = "https://www.retailmenot.com/view/" + seed.path();
            } else if (href.startsWith("/")) {
                href = "https://www.retailmenot.com" + href;
            }

            Coupon coupon = new Coupon();
            coupon.setStore(seed.storeName());
            coupon.setTitle(title);
            coupon.setCategory(seed.category());
            coupon.setExpires(expires);
            coupon.setCouponCode(code);
            coupon.setAffiliateUrl(href);
            coupon.setLogoUrl(seed.logoUrl());
            coupon.setSource("retailmenot");
            parsed.add(coupon);
        }

        return parsed;
    }

    private int applyFallbackCoupons(StoreSeed seed) {
        List<Coupon> fallback = List.of(
            fallbackCoupon(seed, "Extra savings on " + seed.storeName(), "SAVE10", "Limited time"),
            fallbackCoupon(seed, "Selected items promotion at " + seed.storeName(), "DEAL20", "Ends soon")
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
        coupon.setAffiliateUrl("https://www.retailmenot.com/view/" + seed.path());
        coupon.setLogoUrl(seed.logoUrl());
        coupon.setSource("retailmenot-fallback");
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

