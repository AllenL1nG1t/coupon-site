package com.couponsite.admin;

import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponService;
import com.couponsite.coupon.LogoCatalog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    public RetailMeNotCrawlerService(
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
        crawlerLogService.info("RetailMeNot crawler started.");
        int upserts = 0;

        for (StoreSeed seed : STORE_SEEDS) {
            String url = "https://www.retailmenot.com/view/" + seed.path();
            try {
                Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

                List<Coupon> parsedCoupons = parseCoupons(document, seed);
                if (parsedCoupons.isEmpty()) {
                    crawlerLogService.warn("No coupons parsed from " + url);
                    continue;
                }

                for (Coupon coupon : parsedCoupons) {
                    couponService.upsert(coupon);
                    upserts++;
                }
                crawlerLogService.info("Parsed " + parsedCoupons.size() + " coupons from " + seed.storeName());
            } catch (IOException ex) {
                crawlerLogService.error("Crawler failed for " + url + ": " + ex.getMessage());
            }
        }

        crawlerLogService.info("RetailMeNot crawler finished. upserts=" + upserts);
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

