package com.couponsite.admin;

import com.couponsite.brand.BrandProfile;
import com.couponsite.brand.BrandProfileService;
import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponRepository;
import com.couponsite.coupon.LogoCatalog;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BrandCrawlerService {

    private static final String DEFAULT_LOGO = "/logos/default.svg";
    private static final Pattern SIMPLYCODES_STORE_PATH = Pattern.compile("^/store/([^/?#]+)$");
    private static final Pattern HOST_WITH_LOCALE_SUFFIX = Pattern.compile("^(.+\\.[a-z]{2,})(?:-[a-z]{2}(?:-[a-z]{2})?)$");
    private static final int SIMPLYCODES_DISCOVERY_LIMIT = 250;
    private static final Set<String> GENERIC_SUBDOMAINS = Set.of(
        "www", "us", "uk", "ca", "au", "de", "fr", "es", "it", "nl", "br", "jp", "in", "m", "app", "shop", "store"
    );

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
        if (!appSettingService.isRunWindowOpen(
            appSettingService.getBrandCrawlerRunAt(),
            appSettingService.getBrandCrawlerLastRunAt()
        )) {
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
        crawlerLogService.info("[source=brand-profile] Brand profile crawler started.");
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
                crawlerLogService.warn("[source=brand-profile] store=" + seed.store() + " failed=" + ex.getClass().getSimpleName());
            }
        }

        crawlerLogService.info("[source=brand-profile] Brand profile crawler finished. upserts=" + upserts + ", scannedStores=" + seeds.size());
        appSettingService.markBrandCrawlerLastRunNow();
        return upserts;
    }

    private Map<String, BrandSeed> buildSeeds() {
        Map<String, BrandSeed> seeds = new LinkedHashMap<>();
        int popularSeedCount = POPULAR_BRAND_SEEDS.size();

        for (BrandSeed seed : POPULAR_BRAND_SEEDS) {
            seeds.put(seed.store().toLowerCase(Locale.ROOT), seed);
        }

        List<BrandSeed> simplyCodesSeeds = discoverSimplyCodesSeeds();
        for (BrandSeed seed : simplyCodesSeeds) {
            seeds.put(seed.store().toLowerCase(Locale.ROOT), seed);
        }

        int couponDerivedNew = 0;
        for (Coupon coupon : couponRepository.findAllByOrderByCreatedAtDesc()) {
            String store = clean(coupon.getStore());
            if (store.isBlank()) {
                continue;
            }
            String key = store.toLowerCase(Locale.ROOT);
            BrandSeed current = seeds.get(key);
            if (current == null) {
                couponDerivedNew++;
            }
            String logoUrl = preferLogoUrl(clean(coupon.getLogoUrl()), current == null ? "" : current.logoUrl());
            String officialUrl = chooseOfficialUrl(clean(coupon.getAffiliateUrl()), current == null ? "" : current.officialUrl());
            String affiliateUrl = chooseAffiliateUrl(clean(coupon.getAffiliateUrl()), current == null ? "" : current.affiliateUrl(), officialUrl);
            seeds.put(key, new BrandSeed(store, logoUrl, officialUrl, affiliateUrl));
        }

        crawlerLogService.info(
            "[source=brand-profile] Seed summary: popularSeeds=" + popularSeedCount
                + ", discoveredFromSimplyCodes=" + simplyCodesSeeds.size()
                + ", newFromCoupons=" + couponDerivedNew
                + ", mergedUniqueStores=" + seeds.size()
        );

        return seeds;
    }

    private List<BrandSeed> discoverSimplyCodesSeeds() {
        List<BrandSeed> discovered = new ArrayList<>();
        Set<String> seenStores = new LinkedHashSet<>();
        try {
            Document doc = Jsoup.connect("https://simplycodes.com/")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Cache-Control", "no-cache")
                .referrer("https://www.google.com/")
                .timeout(10_000)
                .get();

            for (Element link : doc.select("a[href]")) {
                if (discovered.size() >= SIMPLYCODES_DISCOVERY_LIMIT) {
                    break;
                }
                String rawHref = clean(link.attr("href"));
                String path = extractPath(rawHref);
                Matcher matcher = SIMPLYCODES_STORE_PATH.matcher(path);
                if (!matcher.matches()) {
                    continue;
                }
                String slug = clean(URLDecoder.decode(matcher.group(1), StandardCharsets.UTF_8));
                if (slug.isBlank()) {
                    continue;
                }
                BrandSeed seed = seedFromSimplyCodesSlug(slug);
                String key = seed.store().toLowerCase(Locale.ROOT);
                if (seenStores.add(key)) {
                    discovered.add(seed);
                }
            }
        } catch (IOException ex) {
            crawlerLogService.warn("[source=brand-profile] Brand seed discovery from simplycodes failed: " + ex.getClass().getSimpleName());
        }
        return discovered;
    }

    private BrandSeed seedFromSimplyCodesSlug(String slug) {
        String normalized = clean(slug).toLowerCase(Locale.ROOT);
        String hostLike = normalized;
        Matcher hostWithLocale = HOST_WITH_LOCALE_SUFFIX.matcher(hostLike);
        if (hostWithLocale.matches()) {
            hostLike = hostWithLocale.group(1);
        }
        String officialUrl = "";
        if (hostLike.contains(".")) {
            officialUrl = "https://" + hostLike + "/";
        }
        String store = prettifyStoreName(hostLike.contains(".") ? pickStoreTokenFromHost(hostLike) : normalized);
        if (store.isBlank()) {
            store = "Store";
        }
        String logoUrl = LogoCatalog.forStore(store);
        String affiliateUrl = "https://simplycodes.com/store/" + normalized;
        if (officialUrl.isBlank()) {
            officialUrl = affiliateUrl;
        }
        return new BrandSeed(store, logoUrl, officialUrl, affiliateUrl);
    }

    private String prettifyStoreName(String raw) {
        String value = clean(raw).toLowerCase(Locale.ROOT);
        if (value.isBlank()) {
            return "";
        }
        String noDomain = value.contains(".") ? value.substring(0, value.indexOf('.')) : value;
        String[] chunks = noDomain.split("[-_+]+");
        StringBuilder out = new StringBuilder();
        for (String chunk : chunks) {
            String token = clean(chunk);
            if (token.isBlank()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(' ');
            }
            out.append(Character.toUpperCase(token.charAt(0))).append(token.substring(1));
        }
        return out.toString().trim();
    }

    private String pickStoreTokenFromHost(String host) {
        String[] labels = clean(host).toLowerCase(Locale.ROOT).split("\\.");
        for (String label : labels) {
            String token = clean(label);
            if (token.isBlank() || GENERIC_SUBDOMAINS.contains(token)) {
                continue;
            }
            return token;
        }
        return labels.length == 0 ? "" : clean(labels[0]);
    }

    private String extractPath(String href) {
        if (href.startsWith("http://") || href.startsWith("https://")) {
            try {
                URI uri = URI.create(href);
                return clean(uri.getPath());
            } catch (Exception ex) {
                return "";
            }
        }
        if (href.startsWith("/")) {
            int q = href.indexOf('?');
            if (q >= 0) {
                return href.substring(0, q);
            }
            int hash = href.indexOf('#');
            if (hash >= 0) {
                return href.substring(0, hash);
            }
            return href;
        }
        return "";
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
