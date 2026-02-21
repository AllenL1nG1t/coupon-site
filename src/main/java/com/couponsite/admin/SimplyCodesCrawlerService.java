package com.couponsite.admin;

import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponService;
import com.couponsite.coupon.LogoCatalog;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.HttpStatusException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SimplyCodesCrawlerService {

    private static final List<StoreSeed> STORE_SEEDS = List.of(
        new StoreSeed("Nike", "fashion", LogoCatalog.forStore("Nike"), List.of("nike.com", "nike")),
        new StoreSeed("Samsung", "electronics", LogoCatalog.forStore("Samsung"), List.of("samsung.com", "samsung")),
        new StoreSeed("Best Buy", "electronics", LogoCatalog.forStore("Best Buy"), List.of("bestbuy.com", "best-buy", "bestbuy"))
    );

    private final CouponService couponService;
    private final AppSettingService appSettingService;
    private final CrawlerLogService crawlerLogService;
    private final AtomicLong lastScheduledRunAt = new AtomicLong(0L);

    public SimplyCodesCrawlerService(
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
        crawlerLogService.info("SimplyCodes crawler started.");
        int upserts = 0;
        int duplicates = 0;

        for (StoreSeed seed : STORE_SEEDS) {
            try {
                CrawlTarget crawlTarget = resolveTarget(seed);
                Document document = crawlTarget.document();

                List<Coupon> parsedCoupons = parseCoupons(document, seed);
                if (parsedCoupons.isEmpty()) {
                    crawlerLogService.warn("No coupons parsed from " + crawlTarget.url() + ", fallback applied.");
                    upserts += applyFallbackCoupons(seed, crawlTarget.url());
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
                String fallbackUrl = "https://simplycodes.com/search?q=" + URLEncoder.encode(seed.storeName(), StandardCharsets.UTF_8);
                crawlerLogService.warn("Crawler blocked for " + seed.storeName() + " (" + reason + "), fallback applied.");
                upserts += applyFallbackCoupons(seed, fallbackUrl);
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
                href = "https://simplycodes.com/store/" + seed.paths().get(0);
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

    private int applyFallbackCoupons(StoreSeed seed, String fallbackAffiliateUrl) {
        List<Coupon> fallback = List.of(
            fallbackCoupon(seed, "SimplyCodes: trending deal at " + seed.storeName(), "SIMPLY10", "Limited time", fallbackAffiliateUrl),
            fallbackCoupon(seed, "SimplyCodes: latest promo for " + seed.storeName(), "SCODE20", "Ends soon", fallbackAffiliateUrl)
        );

        int inserted = 0;
        for (Coupon coupon : fallback) {
            if (couponService.upsert(coupon)) {
                inserted++;
            }
        }
        return inserted;
    }

    private Coupon fallbackCoupon(StoreSeed seed, String title, String code, String expires, String affiliateUrl) {
        Coupon coupon = new Coupon();
        coupon.setStore(seed.storeName());
        coupon.setTitle(title);
        coupon.setCategory(seed.category());
        coupon.setExpires(expires);
        coupon.setCouponCode(code);
        coupon.setAffiliateUrl(affiliateUrl);
        coupon.setLogoUrl(seed.logoUrl());
        coupon.setSource("simplycodes-fallback");
        return coupon;
    }

    private CrawlTarget resolveTarget(StoreSeed seed) throws IOException {
        IOException lastException = null;
        for (String candidate : seed.paths()) {
            String url = "https://simplycodes.com/store/" + candidate;
            try {
                Document document = connect(url);
                return new CrawlTarget(url, document);
            } catch (HttpStatusException ex) {
                if (ex.getStatusCode() == 404) {
                    lastException = ex;
                    continue;
                }
                throw ex;
            } catch (IOException ex) {
                lastException = ex;
                break;
            }
        }
        if (lastException != null) {
            throw lastException;
        }
        throw new IOException("No valid simplycodes path for " + seed.storeName());
    }

    private Document connect(String url) throws IOException {
        return Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .header("Accept-Language", "en-US,en;q=0.9")
            .header("Cache-Control", "no-cache")
            .referrer("https://www.google.com/")
            .timeout(10000)
            .get();
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

    private record StoreSeed(String storeName, String category, String logoUrl, List<String> paths) {
    }

    private record CrawlTarget(String url, Document document) {
    }
}
