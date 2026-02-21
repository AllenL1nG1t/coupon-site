package com.couponsite.admin;

import com.couponsite.brand.BrandProfile;
import com.couponsite.brand.BrandProfileService;
import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponService;
import com.couponsite.coupon.LogoCatalog;
import java.net.URI;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenericSiteCrawlerService {

    private final CrawlerSiteService crawlerSiteService;
    private final AppSettingService appSettingService;
    private final CouponService couponService;
    private final BrandProfileService brandProfileService;
    private final BrandLogoCrawlerService brandLogoCrawlerService;
    private final CrawlerLogService crawlerLogService;
    private final AtomicLong lastCouponRunAt = new AtomicLong(0L);
    private final AtomicLong lastBrandRunAt = new AtomicLong(0L);
    private final AtomicLong lastLogoRunAt = new AtomicLong(0L);

    public GenericSiteCrawlerService(
        CrawlerSiteService crawlerSiteService,
        AppSettingService appSettingService,
        CouponService couponService,
        BrandProfileService brandProfileService,
        BrandLogoCrawlerService brandLogoCrawlerService,
        CrawlerLogService crawlerLogService
    ) {
        this.crawlerSiteService = crawlerSiteService;
        this.appSettingService = appSettingService;
        this.couponService = couponService;
        this.brandProfileService = brandProfileService;
        this.brandLogoCrawlerService = brandLogoCrawlerService;
        this.crawlerLogService = crawlerLogService;
    }

    @Scheduled(fixedDelay = 30_000)
    public void scheduledCouponRun() {
        if (!appSettingService.isCouponCrawlerEnabled()) {
            return;
        }
        if (!tryAcquire(lastCouponRunAt, appSettingService.getCouponCrawlerIntervalMs())) {
            return;
        }
        crawlCouponsFromEnabledSites();
    }

    @Scheduled(fixedDelay = 30_000)
    public void scheduledBrandRun() {
        if (!appSettingService.isBrandCrawlerEnabled()) {
            return;
        }
        if (!tryAcquire(lastBrandRunAt, appSettingService.getBrandCrawlerIntervalMs())) {
            return;
        }
        crawlBrandsFromEnabledSites();
    }

    @Scheduled(fixedDelay = 30_000)
    public void scheduledLogoRun() {
        if (!appSettingService.isBrandLogoCrawlerEnabled()) {
            return;
        }
        if (!tryAcquire(lastLogoRunAt, appSettingService.getBrandLogoCrawlerIntervalMs())) {
            return;
        }
        crawlLogosFromEnabledSites();
    }

    @Transactional
    public int crawlCouponsFromEnabledSites() {
        int upserts = 0;
        for (CrawlerSite site : crawlerSiteService.listEntities()) {
            if (!site.isActive() || !site.isCouponEnabled()) {
                continue;
            }
            String key = safe(site.getSiteKey());
            if (isBuiltinSite(key)) {
                continue;
            }
            try {
                String store = deriveStoreName(site);
                String title = deriveTitle(site);

                Coupon coupon = new Coupon();
                coupon.setStore(store);
                coupon.setTitle("Latest deals: " + title);
                coupon.setCategory("other");
                coupon.setExpires("Limited time");
                coupon.setCouponCode("WELCOME10");
                coupon.setAffiliateUrl("");
                coupon.setLogoUrl(LogoCatalog.forStore(store));
                coupon.setSource("custom-" + key);
                if (couponService.upsert(coupon)) {
                    upserts++;
                }
            } catch (Exception ex) {
                crawlerLogService.warn("Custom coupon crawl failed for " + safe(site.getBaseUrl()) + ": " + ex.getClass().getSimpleName());
            }
        }
        if (upserts > 0) {
            crawlerLogService.info("Custom coupon crawler finished. upserts=" + upserts);
        }
        return upserts;
    }

    @Transactional
    public int crawlBrandsFromEnabledSites() {
        int upserts = 0;
        for (CrawlerSite site : crawlerSiteService.listEntities()) {
            if (!site.isActive() || !site.isBrandEnabled()) {
                continue;
            }
            if (isBuiltinSite(site.getSiteKey())) {
                continue;
            }
            try {
                String store = deriveStoreName(site);
                BrandProfile profile = brandProfileService.findEntityByStore(store).orElseGet(BrandProfile::new);
                boolean changed = false;
                if (isBlank(profile.getStoreName())) {
                    profile.setStoreName(store);
                    changed = true;
                }
                if (isBlank(profile.getSlug())) {
                    profile.setSlug(brandProfileService.normalizeSlug(store));
                    changed = true;
                }
                if (isBlank(profile.getTitle())) {
                    profile.setTitle(store + " Coupons");
                    changed = true;
                }
                if (isBlank(profile.getSummary())) {
                    profile.setSummary("Latest promo codes and deals for " + store + ".");
                    changed = true;
                }
                if (isBlank(profile.getDescription())) {
                    profile.setDescription("Auto-generated profile from custom crawler site.");
                    changed = true;
                }
                if (isBlank(profile.getHeroImageUrl())) {
                    profile.setHeroImageUrl("/logos/default.svg");
                    changed = true;
                }
                if (isBlank(profile.getLogoUrl())) {
                    profile.setLogoUrl(LogoCatalog.forStore(store));
                    changed = true;
                }
                if (isBlank(profile.getOfficialUrl())) {
                    profile.setOfficialUrl(site.getBaseUrl());
                    changed = true;
                }
                if (isBlank(profile.getAffiliateUrl())) {
                    profile.setAffiliateUrl(site.getBaseUrl());
                    changed = true;
                }
                if (changed) {
                    brandProfileService.save(profile);
                    upserts++;
                }
            } catch (Exception ex) {
                crawlerLogService.warn("Custom brand crawl failed for " + safe(site.getBaseUrl()) + ": " + ex.getClass().getSimpleName());
            }
        }
        if (upserts > 0) {
            crawlerLogService.info("Custom brand crawler finished. upserts=" + upserts);
        }
        return upserts;
    }

    @Transactional
    public int crawlLogosFromEnabledSites() {
        int upserts = 0;
        for (CrawlerSite site : crawlerSiteService.listEntities()) {
            if (!site.isActive() || !site.isLogoEnabled()) {
                continue;
            }
            if (isBuiltinSite(site.getSiteKey())) {
                continue;
            }
            try {
                String store = deriveStoreName(site);
                BrandProfile profile = brandProfileService.findEntityByStore(store).orElseGet(BrandProfile::new);
                if (isBlank(profile.getStoreName())) {
                    profile.setStoreName(store);
                }
                if (isBlank(profile.getSlug())) {
                    profile.setSlug(brandProfileService.normalizeSlug(store));
                }
                if (isBlank(profile.getTitle())) {
                    profile.setTitle(store + " Coupons");
                }
                if (isBlank(profile.getSummary())) {
                    profile.setSummary("Latest promo codes and deals for " + store + ".");
                }
                if (isBlank(profile.getDescription())) {
                    profile.setDescription("Auto-generated profile from custom crawler site.");
                }
                if (isBlank(profile.getHeroImageUrl())) {
                    profile.setHeroImageUrl("/logos/default.svg");
                }
                if (isBlank(profile.getLogoUrl())) {
                    profile.setLogoUrl(LogoCatalog.forStore(store));
                }
                profile.setOfficialUrl(site.getBaseUrl());
                profile.setAffiliateUrl(site.getBaseUrl());
                profile = brandProfileService.save(profile);
                if (brandLogoCrawlerService.crawlSingle(profile)) {
                    upserts++;
                }
            } catch (Exception ex) {
                crawlerLogService.warn("Custom logo crawl failed for " + safe(site.getBaseUrl()) + ": " + ex.getClass().getSimpleName());
            }
        }
        if (upserts > 0) {
            crawlerLogService.info("Custom logo crawler finished. logosStored=" + upserts);
        }
        return upserts;
    }

    private String deriveStoreName(CrawlerSite site) {
        String explicit = safe(site.getSiteName());
        if (!explicit.isBlank()) {
            return explicit;
        }
        try {
            URI uri = URI.create(site.getBaseUrl());
            String host = safe(uri.getHost()).toLowerCase(Locale.ROOT);
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            String raw = host.split("\\.")[0];
            if (raw.isBlank()) {
                return "Store";
            }
            return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
        } catch (Exception ex) {
            return "Store";
        }
    }

    private String deriveTitle(CrawlerSite site) {
        try {
            Document doc = Jsoup.connect(site.getBaseUrl())
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .timeout(9000)
                .get();
            String title = safe(doc.title());
            if (!title.isBlank()) {
                return title;
            }
        } catch (Exception ignored) {
            // fallback
        }
        return deriveStoreName(site);
    }

    private boolean isBlank(String value) {
        return safe(value).isBlank();
    }

    private boolean isBuiltinSite(String siteKey) {
        String key = safe(siteKey).toLowerCase(Locale.ROOT);
        return "retailmenot".equals(key) || "simplycodes".equals(key);
    }

    private boolean tryAcquire(AtomicLong slot, long intervalMs) {
        long now = System.currentTimeMillis();
        long lastRun = slot.get();
        if (now - lastRun < intervalMs) {
            return false;
        }
        return slot.compareAndSet(lastRun, now);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
