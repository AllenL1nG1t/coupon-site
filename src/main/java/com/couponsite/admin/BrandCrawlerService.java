package com.couponsite.admin;

import com.couponsite.brand.BrandProfile;
import com.couponsite.brand.BrandProfileService;
import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponRepository;
import com.couponsite.coupon.LogoCatalog;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BrandCrawlerService {

    private static final String DEFAULT_LOGO = "/logos/default.svg";

    private static final List<BrandSeed> POPULAR_BRAND_SEEDS = List.of(
        seed("Nike", "https://www.nike.com/"),
        seed("Adidas", "https://www.adidas.com/"),
        seed("Puma", "https://us.puma.com/"),
        seed("New Balance", "https://www.newbalance.com/"),
        seed("Best Buy", "https://www.bestbuy.com/"),
        seed("Walmart", "https://www.walmart.com/"),
        seed("Target", "https://www.target.com/"),
        seed("Costco", "https://www.costco.com/"),
        seed("Dell", "https://www.dell.com/"),
        seed("Lenovo", "https://www.lenovo.com/"),
        seed("HP", "https://www.hp.com/"),
        seed("Samsung", "https://www.samsung.com/"),
        seed("Apple", "https://www.apple.com/"),
        seed("Sephora", "https://www.sephora.com/"),
        seed("Ulta Beauty", "https://www.ulta.com/"),
        seed("Macy's", "https://www.macys.com/"),
        seed("Nordstrom", "https://www.nordstrom.com/"),
        seed("Expedia", "https://www.expedia.com/"),
        seed("Booking.com", "https://www.booking.com/"),
        seed("Hotels.com", "https://www.hotels.com/"),
        seed("Trip.com", "https://www.trip.com/"),
        seed("DoorDash", "https://www.doordash.com/"),
        seed("Uber", "https://www.uber.com/"),
        seed("Grubhub", "https://www.grubhub.com/"),
        seed("Instacart", "https://www.instacart.com/"),
        seed("AliExpress", "https://www.aliexpress.com/"),
        seed("eBay", "https://www.ebay.com/"),
        seed("Amazon", "https://www.amazon.com/")
    );

    private final CouponRepository couponRepository;
    private final BrandProfileService brandProfileService;
    private final AppSettingService appSettingService;
    private final CrawlerLogService crawlerLogService;
    private final AtomicLong lastScheduledRunAt = new AtomicLong(0L);

    public BrandCrawlerService(
        CouponRepository couponRepository,
        BrandProfileService brandProfileService,
        AppSettingService appSettingService,
        CrawlerLogService crawlerLogService
    ) {
        this.couponRepository = couponRepository;
        this.brandProfileService = brandProfileService;
        this.appSettingService = appSettingService;
        this.crawlerLogService = crawlerLogService;
    }

    @Scheduled(fixedDelay = 30_000)
    public void scheduledRun() {
        if (!appSettingService.isBrandCrawlerEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        long intervalMs = appSettingService.getBrandCrawlerIntervalMs();
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
        crawlerLogService.info("Brand profile crawler started.");
        Map<String, BrandSeed> seeds = buildSeeds();
        int upserts = 0;

        for (BrandSeed seed : seeds.values()) {
            try {
                BrandProfile profile = brandProfileService.findEntityByStore(seed.store())
                    .orElseGet(BrandProfile::new);
                boolean changed = applySeed(profile, seed);
                if (changed) {
                    brandProfileService.save(profile);
                    upserts++;
                }
            } catch (Exception ex) {
                crawlerLogService.warn("Brand profile crawl failed for " + seed.store() + ": " + ex.getClass().getSimpleName());
            }
        }

        crawlerLogService.info("Brand profile crawler finished. upserts=" + upserts + ", scannedStores=" + seeds.size());
        return upserts;
    }

    private Map<String, BrandSeed> buildSeeds() {
        Map<String, BrandSeed> seeds = new LinkedHashMap<>();

        for (BrandSeed seed : POPULAR_BRAND_SEEDS) {
            seeds.put(seed.store().toLowerCase(Locale.ROOT), seed);
        }

        for (Coupon coupon : couponRepository.findAllByOrderByCreatedAtDesc()) {
            String store = clean(coupon.getStore());
            if (store.isBlank()) {
                continue;
            }
            String key = store.toLowerCase(Locale.ROOT);
            BrandSeed current = seeds.get(key);
            String logoUrl = preferLogoUrl(clean(coupon.getLogoUrl()), current == null ? "" : current.logoUrl());
            String officialUrl = chooseOfficialUrl(clean(coupon.getAffiliateUrl()), current == null ? "" : current.officialUrl());
            String affiliateUrl = chooseAffiliateUrl(clean(coupon.getAffiliateUrl()), current == null ? "" : current.affiliateUrl(), officialUrl);
            seeds.put(key, new BrandSeed(store, logoUrl, officialUrl, affiliateUrl));
        }

        return seeds;
    }

    private boolean applySeed(BrandProfile profile, BrandSeed seed) {
        boolean changed = false;

        if (isBlank(profile.getStoreName())) {
            profile.setStoreName(seed.store());
            changed = true;
        }
        if (isBlank(profile.getSlug())) {
            profile.setSlug(brandProfileService.normalizeSlug(seed.store()));
            changed = true;
        }
        if (isBlank(profile.getTitle())) {
            profile.setTitle(seed.store() + " Coupons");
            changed = true;
        }
        if (isBlank(profile.getSummary())) {
            profile.setSummary("Latest promo codes and deals for " + seed.store() + ".");
            changed = true;
        }
        if (isBlank(profile.getDescription())) {
            profile.setDescription("Auto-generated brand profile from crawler. You can edit this content from admin panel.");
            changed = true;
        }
        if (isBlank(profile.getHeroImageUrl())) {
            profile.setHeroImageUrl(DEFAULT_LOGO);
            changed = true;
        }
        if (isBlank(profile.getLogoUrl()) || DEFAULT_LOGO.equals(profile.getLogoUrl())) {
            String seedLogo = isBlank(seed.logoUrl()) ? LogoCatalog.forStore(seed.store()) : seed.logoUrl();
            profile.setLogoUrl(seedLogo);
            changed = true;
        }
        if (isBlank(profile.getOfficialUrl()) || "https://example.com".equals(profile.getOfficialUrl())) {
            profile.setOfficialUrl(isBlank(seed.officialUrl()) ? "https://example.com" : seed.officialUrl());
            changed = true;
        }
        if (isBlank(profile.getAffiliateUrl())) {
            profile.setAffiliateUrl(isBlank(seed.affiliateUrl()) ? profile.getOfficialUrl() : seed.affiliateUrl());
            changed = true;
        }

        return changed;
    }

    private String chooseOfficialUrl(String couponAffiliate, String existing) {
        String derived = toOfficialSite(couponAffiliate);
        if (!isBlank(derived)) {
            return derived;
        }
        return existing;
    }

    private String chooseAffiliateUrl(String couponAffiliate, String existing, String officialUrl) {
        if (!isBlank(couponAffiliate)) {
            return couponAffiliate;
        }
        if (!isBlank(existing)) {
            return existing;
        }
        return officialUrl;
    }

    private static BrandSeed seed(String store, String officialUrl) {
        return new BrandSeed(store, LogoCatalog.forStore(store), officialUrl, officialUrl);
    }

    private String toOfficialSite(String rawUrl) {
        try {
            if (isBlank(rawUrl)) {
                return "";
            }
            URI uri = URI.create(rawUrl);
            String host = clean(uri.getHost()).toLowerCase(Locale.ROOT);
            if (isBlank(host) || host.contains("retailmenot.com") || host.contains("simplycodes.com")) {
                return "";
            }
            return "https://" + host + "/";
        } catch (Exception ex) {
            return "";
        }
    }

    private String preferLogoUrl(String candidate, String existing) {
        if (!isBlank(candidate) && !DEFAULT_LOGO.equals(candidate)) {
            return candidate;
        }
        return existing;
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return clean(value).isBlank();
    }

    private record BrandSeed(String store, String logoUrl, String officialUrl, String affiliateUrl) {
    }
}
