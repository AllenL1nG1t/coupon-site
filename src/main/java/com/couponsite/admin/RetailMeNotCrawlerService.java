package com.couponsite.admin;

import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.LogoCatalog;
import com.couponsite.coupon.StagedCouponService;
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
        new StoreSeed("bestbuy.com", "Best Buy", "electronics", LogoCatalog.forStore("Best Buy")),
        new StoreSeed("adidas.com", "Adidas", "fashion", LogoCatalog.forStore("Adidas")),
        new StoreSeed("walmart.com", "Walmart", "other", LogoCatalog.forStore("Walmart")),
        new StoreSeed("target.com", "Target", "other", LogoCatalog.forStore("Target")),
        new StoreSeed("lenovo.com", "Lenovo", "electronics", LogoCatalog.forStore("Lenovo")),
        new StoreSeed("dell.com", "Dell", "electronics", LogoCatalog.forStore("Dell")),
        new StoreSeed("sephora.com", "Sephora", "fashion", LogoCatalog.forStore("Sephora")),
        new StoreSeed("ulta.com", "Ulta Beauty", "fashion", LogoCatalog.forStore("Ulta Beauty")),
        new StoreSeed("booking.com", "Booking.com", "travel", LogoCatalog.forStore("Booking.com")),
        new StoreSeed("hotels.com", "Hotels.com", "travel", LogoCatalog.forStore("Hotels.com")),
        new StoreSeed("uber.com", "Uber", "travel", LogoCatalog.forStore("Uber")),
        new StoreSeed("grubhub.com", "Grubhub", "food", LogoCatalog.forStore("Grubhub"))
    );

    private final StagedCouponService stagedCouponService;
    private final AppSettingService appSettingService;
    private final CrawlerSiteService crawlerSiteService;
    private final CrawlerLogService crawlerLogService;
    private final AtomicLong lastScheduledRunAt = new AtomicLong(0L);

    public RetailMeNotCrawlerService(
        StagedCouponService stagedCouponService,
        AppSettingService appSettingService,
        CrawlerSiteService crawlerSiteService,
        CrawlerLogService crawlerLogService
    ) {
        this.stagedCouponService = stagedCouponService;
        this.appSettingService = appSettingService;
        this.crawlerSiteService = crawlerSiteService;
        this.crawlerLogService = crawlerLogService;
    }

    @Scheduled(fixedDelay = 30_000)
    public void scheduledRun() {
        if (!appSettingService.isCouponCrawlerEnabled()) {
            return;
        }
        if (!appSettingService.isRunWindowOpen(
            appSettingService.getCouponCrawlerRunAt(),
            appSettingService.getCouponCrawlerLastRunAt()
        )) {
            return;
        }
        long now = System.currentTimeMillis();
        long intervalMs = appSettingService.getCouponCrawlerIntervalMs();
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
        if (!crawlerSiteService.isEnabled("retailmenot", CrawlerSiteService.DataType.COUPON)) {
            crawlerLogService.info("[source=retailmenot] Coupon crawler skipped by site switch.");
            return 0;
        }
        crawlerLogService.info("[source=retailmenot] Coupon crawler started. seedStores=" + STORE_SEEDS.size());
        int upserts = 0;
        int duplicates = 0;
        int scannedStores = 0;

        for (StoreSeed seed : STORE_SEEDS) {
            String url = "https://www.retailmenot.com/view/" + seed.path();
            scannedStores++;
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
                    int fallbackInserted = applyFallbackCoupons(seed);
                    upserts += fallbackInserted;
                    crawlerLogService.warn("[source=retailmenot] store=" + seed.storeName() + " parsed=0 fallbackInserted=" + fallbackInserted + " url=" + url);
                } else {
                    int storeUpserts = 0;
                    int storeDuplicates = 0;
                    for (Coupon coupon : parsedCoupons) {
                        if (stagedCouponService.stageFromCrawler(coupon)) {
                            upserts++;
                            storeUpserts++;
                        } else {
                            duplicates++;
                            storeDuplicates++;
                        }
                    }
                    crawlerLogService.info(
                        "[source=retailmenot] store=" + seed.storeName()
                            + " parsed=" + parsedCoupons.size()
                            + " upserts=" + storeUpserts
                            + " duplicates=" + storeDuplicates
                    );
                }
            } catch (IOException ex) {
                String reason = classifyError(ex);
                int fallbackInserted = applyFallbackCoupons(seed);
                upserts += fallbackInserted;
                crawlerLogService.warn("[source=retailmenot] store=" + seed.storeName() + " blocked=" + reason + " fallbackInserted=" + fallbackInserted + " url=" + url);
            }
        }

        crawlerLogService.info(
            "[source=retailmenot] Coupon crawler finished. scannedStores=" + scannedStores
                + ", upserts=" + upserts
                + ", skippedDuplicates=" + duplicates
        );
        appSettingService.markCouponCrawlerLastRunNow();
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
                href = "";
            } else if (href.startsWith("/")) {
                href = "https://www.retailmenot.com" + href;
            }

            Coupon coupon = new Coupon();
            coupon.setStore(seed.storeName());
            coupon.setTitle(title);
            coupon.setCategory(seed.category());
            coupon.setExpires(expires);
            coupon.setCouponCode(code);
            coupon.setAffiliateUrl("");
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
            if (stagedCouponService.stageFromCrawler(coupon)) {
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
        coupon.setAffiliateUrl("");
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

