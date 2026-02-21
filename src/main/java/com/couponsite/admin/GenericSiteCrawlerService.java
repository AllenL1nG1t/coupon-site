package com.couponsite.admin;

import com.couponsite.brand.BrandProfile;
import com.couponsite.brand.BrandProfileService;
import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponService;
import com.couponsite.coupon.LogoCatalog;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenericSiteCrawlerService {

    private static final int FETCH_TIMEOUT_MS = 12_000;
    private static final int MAX_DISCOVERY_PAGES = 8;
    private static final int MAX_COUPONS_PER_SITE = 60;
    private static final int MAX_BRANDS_PER_SITE = 80;

    private static final Pattern CODE_PATTERN = Pattern.compile("(?i)(?:code|coupon|promo)\\s*[:#\\-]?\\s*([A-Za-z0-9]{4,16})");
    private static final Pattern RAW_CODE_PATTERN = Pattern.compile("\\b[A-Z0-9]{5,14}\\b");
    private static final Pattern DATE_HINT_PATTERN = Pattern.compile("(?i)(expires?|valid\\s+until|ends?)\\s*[:\\-]?\\s*([A-Za-z0-9,\\s]{3,40})");
    private static final List<String> COUPON_TITLE_BLOCKLIST = List.of(
        "recommended stores",
        "featured shops",
        "see all stores",
        "newly added stores",
        "gift card shop",
        "our rating",
        "how it works",
        "buy online, pick up in-store",
        "members",
        "top stores"
    );
    private static final List<String> STORE_NAME_BLOCKLIST = List.of(
        "cash back",
        "recommended stores",
        "see all stores",
        "featured shops"
    );

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
        int inserted = 0;
        int duplicates = 0;
        int scannedSites = 0;

        for (CrawlerSite site : crawlerSiteService.listEntities()) {
            if (!site.isActive() || !site.isCouponEnabled()) {
                continue;
            }
            String siteKey = safe(site.getSiteKey()).toLowerCase(Locale.ROOT);
            if (isBuiltinSite(siteKey)) {
                continue;
            }
            scannedSites++;

            try {
                Map<String, Document> pages = discoverPages(site);
                List<Coupon> parsedCoupons = parseCoupons(site, pages);
                int siteInserted = 0;
                int siteDuplicates = 0;

                for (Coupon coupon : parsedCoupons) {
                    if (couponService.upsert(coupon)) {
                        inserted++;
                        siteInserted++;
                    } else {
                        duplicates++;
                        siteDuplicates++;
                    }
                }

                crawlerLogService.info(
                    "[source=custom-coupon] site=" + siteKey
                        + " pages=" + pages.size()
                        + " parsed=" + parsedCoupons.size()
                        + " upserts=" + siteInserted
                        + " duplicates=" + siteDuplicates
                        + " baseUrl=" + safe(site.getBaseUrl())
                );
            } catch (Exception ex) {
                crawlerLogService.warn(
                    "[source=custom-coupon] site=" + siteKey
                        + " failed=" + ex.getClass().getSimpleName()
                        + " baseUrl=" + safe(site.getBaseUrl())
                );
            }
        }

        crawlerLogService.info(
            "[source=custom-coupon] Custom coupon crawler finished. scannedSites=" + scannedSites
                + ", upserts=" + inserted
                + ", skippedDuplicates=" + duplicates
        );
        return inserted;
    }

    @Transactional
    public int crawlBrandsFromEnabledSites() {
        int upserts = 0;
        int scannedSites = 0;

        for (CrawlerSite site : crawlerSiteService.listEntities()) {
            if (!site.isActive() || !site.isBrandEnabled()) {
                continue;
            }
            String siteKey = safe(site.getSiteKey()).toLowerCase(Locale.ROOT);
            if (isBuiltinSite(siteKey)) {
                continue;
            }
            scannedSites++;

            try {
                Map<String, Document> pages = discoverPages(site);
                Set<String> storeNames = discoverBrandNames(site, pages);
                int siteUpserts = 0;

                for (String storeName : storeNames) {
                    if (upsertBrand(site, storeName).changed()) {
                        upserts++;
                        siteUpserts++;
                    }
                }

                crawlerLogService.info(
                    "[source=custom-brand] site=" + siteKey
                        + " pages=" + pages.size()
                        + " brandsFound=" + storeNames.size()
                        + " upserts=" + siteUpserts
                        + " baseUrl=" + safe(site.getBaseUrl())
                );
            } catch (Exception ex) {
                crawlerLogService.warn(
                    "[source=custom-brand] site=" + siteKey
                        + " failed=" + ex.getClass().getSimpleName()
                        + " baseUrl=" + safe(site.getBaseUrl())
                );
            }
        }

        crawlerLogService.info("[source=custom-brand] Custom brand crawler finished. scannedSites=" + scannedSites + ", upserts=" + upserts);
        return upserts;
    }

    @Transactional
    public int crawlLogosFromEnabledSites() {
        int logosStored = 0;
        int scannedSites = 0;

        for (CrawlerSite site : crawlerSiteService.listEntities()) {
            if (!site.isActive() || !site.isLogoEnabled()) {
                continue;
            }
            String siteKey = safe(site.getSiteKey()).toLowerCase(Locale.ROOT);
            if (isBuiltinSite(siteKey)) {
                continue;
            }
            scannedSites++;

            try {
                Map<String, Document> pages = discoverPages(site);
                Set<String> storeNames = discoverBrandNames(site, pages);
                int siteLogos = 0;

                for (String storeName : storeNames) {
                    BrandProfile profile = upsertBrand(site, storeName).profile();
                    if (brandLogoCrawlerService.crawlSingle(profile)) {
                        logosStored++;
                        siteLogos++;
                    }
                }

                crawlerLogService.info(
                    "[source=custom-logo] site=" + siteKey
                        + " pages=" + pages.size()
                        + " brandsFound=" + storeNames.size()
                        + " logosStored=" + siteLogos
                        + " baseUrl=" + safe(site.getBaseUrl())
                );
            } catch (Exception ex) {
                crawlerLogService.warn(
                    "[source=custom-logo] site=" + siteKey
                        + " failed=" + ex.getClass().getSimpleName()
                        + " baseUrl=" + safe(site.getBaseUrl())
                );
            }
        }

        crawlerLogService.info("[source=custom-logo] Custom logo crawler finished. scannedSites=" + scannedSites + ", logosStored=" + logosStored);
        return logosStored;
    }

    private Map<String, Document> discoverPages(CrawlerSite site) {
        Map<String, Document> pages = new LinkedHashMap<>();
        String baseUrl = normalizeUrl(site.getBaseUrl());
        if (baseUrl.isBlank()) {
            return pages;
        }

        fetchAndAddPage(baseUrl, pages);

        for (String suffix : List.of("/coupons", "/deals", "/offers", "/promo-codes", "/promotions", "/stores", "/brands")) {
            if (pages.size() >= MAX_DISCOVERY_PAGES) {
                break;
            }
            fetchAndAddPage(resolveUrl(baseUrl, suffix), pages);
        }

        Document homepage = pages.get(baseUrl);
        if (homepage != null && pages.size() < MAX_DISCOVERY_PAGES) {
            List<String> discoveredLinks = homepage.select("a[href]").stream()
                .map(a -> a.absUrl("href").isBlank() ? resolveUrl(baseUrl, a.attr("href")) : a.absUrl("href"))
                .filter(this::isLikelyCrawlerTarget)
                .sorted(Comparator.comparingInt(String::length))
                .distinct()
                .limit(MAX_DISCOVERY_PAGES)
                .toList();

            for (String link : discoveredLinks) {
                if (pages.size() >= MAX_DISCOVERY_PAGES) {
                    break;
                }
                fetchAndAddPage(link, pages);
            }
        }

        return pages;
    }

    private void fetchAndAddPage(String url, Map<String, Document> pages) {
        String normalized = normalizeUrl(url);
        if (normalized.isBlank() || pages.containsKey(normalized)) {
            return;
        }
        try {
            Document doc = connect(normalized);
            pages.put(normalized, doc);
        } catch (Exception ex) {
            crawlerLogService.warn("[source=custom-discovery] fetch_failed url=" + normalized + " reason=" + ex.getClass().getSimpleName());
        }
    }

    private List<Coupon> parseCoupons(CrawlerSite site, Map<String, Document> pages) {
        List<Coupon> coupons = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();

        String fallbackStore = deriveStoreName(site);
        String source = "custom-" + safe(site.getSiteKey()).toLowerCase(Locale.ROOT);
        boolean aggregatorSite = isAggregatorDomain(site.getBaseUrl());

        for (Map.Entry<String, Document> entry : pages.entrySet()) {
            String pageUrl = entry.getKey();
            Document doc = entry.getValue();

            List<Element> nodes = doc.select(
                "article, section, li, " +
                    "div[class*=coupon], div[class*=deal], div[class*=offer], div[class*=promo], " +
                    "div[data-testid*=coupon], div[data-testid*=offer], div[data-testid*=deal]"
            );
            for (Element node : nodes) {
                if (coupons.size() >= MAX_COUPONS_PER_SITE) {
                    return coupons;
                }
                if (!looksLikeCouponNode(node)) {
                    continue;
                }

                String title = extractTitle(node);
                if (title.isBlank()) {
                    continue;
                }

                String store = extractStoreName(node, pageUrl, fallbackStore);
                String storeFromTitle = inferStoreFromTitle(title);
                if (isUsableBrandName(storeFromTitle)) {
                    store = storeFromTitle;
                }
                String code = extractCouponCode(node.text(), title, store, pageUrl);
                if (aggregatorSite && normalizeKey(store).equals(normalizeKey(fallbackStore))) {
                    continue;
                }
                if (isDirtyCoupon(store, title, code, fallbackStore)) {
                    continue;
                }
                String dedupe = normalizeKey(store) + "|" + normalizeKey(code);
                if (!seenKeys.add(dedupe)) {
                    continue;
                }

                Coupon coupon = new Coupon();
                coupon.setStore(store);
                coupon.setTitle(title);
                coupon.setCategory(detectCategory(node.text(), title));
                coupon.setExpires(extractExpires(node.text()));
                coupon.setCouponCode(code);
                coupon.setAffiliateUrl(extractAffiliateUrl(node, pageUrl, site.getBaseUrl()));
                coupon.setLogoUrl(LogoCatalog.forStore(store));
                coupon.setSource(source);
                coupons.add(coupon);
            }
        }

        if (coupons.isEmpty()) {
            Coupon fallback = new Coupon();
            fallback.setStore(fallbackStore);
            fallback.setTitle("Latest deals at " + fallbackStore);
            fallback.setCategory("other");
            fallback.setExpires("Limited time");
            fallback.setCouponCode(extractCouponCode("", fallback.getTitle(), fallbackStore, safe(site.getBaseUrl())));
            fallback.setAffiliateUrl(normalizeUrl(site.getBaseUrl()));
            fallback.setLogoUrl(LogoCatalog.forStore(fallbackStore));
            fallback.setSource(source + "-fallback");
            coupons.add(fallback);
        }

        return coupons;
    }

    private Set<String> discoverBrandNames(CrawlerSite site, Map<String, Document> pages) {
        Set<String> names = new LinkedHashSet<>();
        String fallbackStore = deriveStoreName(site);

        for (Map.Entry<String, Document> entry : pages.entrySet()) {
            if (names.size() >= MAX_BRANDS_PER_SITE) {
                break;
            }

            String pageUrl = entry.getKey();
            Document doc = entry.getValue();

            for (Element anchor : doc.select("a[href]")) {
                if (names.size() >= MAX_BRANDS_PER_SITE) {
                    break;
                }
                String href = anchor.absUrl("href").isBlank() ? resolveUrl(pageUrl, anchor.attr("href")) : anchor.absUrl("href");
                String text = cleanName(anchor.text());
                if (isLikelyBrandLink(href) && isUsableBrandName(text)) {
                    names.add(text);
                } else {
                    String fromHref = extractStoreFromUrl(href);
                    if (isUsableBrandName(fromHref)) {
                        names.add(fromHref);
                    }
                }
            }
        }

        if (names.isEmpty()) {
            names.add(fallbackStore);
        }

        return names;
    }

    private BrandUpsertResult upsertBrand(CrawlerSite site, String storeName) {
        String store = isUsableBrandName(storeName) ? storeName : deriveStoreName(site);
        Optional<BrandProfile> existing = brandProfileService.findEntityByStore(store);
        BrandProfile profile = existing.orElseGet(BrandProfile::new);
        boolean changed = existing.isEmpty();

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
            profile.setDescription("Auto-generated from generic custom crawler.");
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
            profile.setOfficialUrl(normalizeUrl(site.getBaseUrl()));
            changed = true;
        }
        if (isBlank(profile.getAffiliateUrl())) {
            profile.setAffiliateUrl(normalizeUrl(site.getBaseUrl()));
            changed = true;
        }

        return new BrandUpsertResult(brandProfileService.save(profile), changed);
    }

    private boolean looksLikeCouponNode(Element node) {
        String text = safe(node.text()).toLowerCase(Locale.ROOT);
        if (text.length() < 14 || text.length() > 320) {
            return false;
        }

        int score = 0;
        if (text.contains("coupon")) score += 2;
        if (text.contains("promo")) score += 2;
        if (text.contains("offer")) score += 1;
        if (text.contains("deal")) score += 1;
        if (text.contains("save") || text.contains("off") || text.contains("discount")) score += 1;
        if (CODE_PATTERN.matcher(text).find() || RAW_CODE_PATTERN.matcher(text.toUpperCase(Locale.ROOT)).find()) score += 2;
        return score >= 3;
    }

    private String extractTitle(Element node) {
        for (String selector : List.of("h1", "h2", "h3", "h4", ".title", "[class*=title]", "strong")) {
            Element el = node.selectFirst(selector);
            if (el != null) {
                String text = safe(el.text());
                if (text.length() >= 6) {
                    return capLength(text, 180);
                }
            }
        }
        String fallback = safe(node.ownText());
        if (fallback.length() < 6) {
            fallback = safe(node.text());
        }
        return capLength(fallback, 180);
    }

    private String extractCouponCode(String allText, String title, String store, String pageUrl) {
        String upperText = safe(allText).toUpperCase(Locale.ROOT);

        Matcher labeled = CODE_PATTERN.matcher(allText);
        if (labeled.find()) {
            String candidate = normalizeCode(labeled.group(1));
            if (!candidate.isBlank()) {
                return candidate;
            }
        }

        Matcher raw = RAW_CODE_PATTERN.matcher(upperText);
        while (raw.find()) {
            String candidate = normalizeCode(raw.group());
            if (isLikelyCode(candidate) && containsDigit(candidate)) {
                return candidate;
            }
        }

        long hash = Integer.toUnsignedLong((safe(store) + "|" + safe(title) + "|" + safe(pageUrl)).hashCode());
        String generated = "AUTO" + Long.toString(hash, 36).toUpperCase(Locale.ROOT);
        return capLength(generated, 14);
    }

    private String extractExpires(String text) {
        Matcher matcher = DATE_HINT_PATTERN.matcher(safe(text));
        if (matcher.find()) {
            String value = safe(matcher.group(2));
            if (!value.isBlank()) {
                return capLength(value, 50);
            }
        }
        return "Limited time";
    }

    private String detectCategory(String text, String title) {
        String merged = (safe(text) + " " + safe(title)).toLowerCase(Locale.ROOT);
        if (containsAny(merged, "flight", "hotel", "travel", "trip", "booking")) return "travel";
        if (containsAny(merged, "pizza", "food", "restaurant", "meal", "delivery")) return "food";
        if (containsAny(merged, "laptop", "phone", "tech", "electronics", "monitor", "tv")) return "electronics";
        if (containsAny(merged, "shoe", "fashion", "beauty", "apparel", "clothing", "cosmetic")) return "fashion";
        return "other";
    }

    private String extractAffiliateUrl(Element node, String pageUrl, String siteBaseUrl) {
        for (Element link : node.select("a[href]")) {
            String href = link.absUrl("href");
            if (href.isBlank()) {
                href = resolveUrl(pageUrl, link.attr("href"));
            }
            href = normalizeUrl(href);
            if (href.isBlank()) {
                continue;
            }
            return href;
        }
        return normalizeUrl(siteBaseUrl);
    }

    private String extractStoreName(Element node, String pageUrl, String fallbackStore) {
        for (String selector : List.of("[data-store]", "[class*=store]", "[class*=brand]", "[id*=store]", "[id*=brand]")) {
            Element el = node.selectFirst(selector);
            if (el != null) {
                String value = cleanName(el.hasAttr("data-store") ? el.attr("data-store") : el.text());
                if (isUsableBrandName(value)) {
                    return value;
                }
            }
        }

        for (Element link : node.select("a[href]")) {
            String href = link.absUrl("href");
            if (href.isBlank()) {
                href = resolveUrl(pageUrl, link.attr("href"));
            }
            String fromPath = extractStoreFromUrl(href);
            if (isUsableBrandName(fromPath)) {
                return fromPath;
            }

            String text = cleanName(link.text());
            if (isUsableBrandName(text)) {
                return text;
            }
        }

        return fallbackStore;
    }

    private String inferStoreFromTitle(String title) {
        String text = safe(title);
        if (text.isBlank()) {
            return "";
        }
        Matcher matcher = Pattern.compile("(?i)see\\s+all\\s+(.{2,60}?)\\s+coupons").matcher(text);
        if (matcher.find()) {
            String value = cleanName(matcher.group(1));
            if (isUsableBrandName(value)) {
                return value;
            }
        }
        return "";
    }

    private String extractStoreFromUrl(String rawUrl) {
        String url = normalizeUrl(rawUrl);
        if (url.isBlank()) {
            return "";
        }

        String fromQuery = queryParam(url, "store");
        if (isUsableBrandName(fromQuery)) {
            return slugToName(fromQuery);
        }

        String lower = url.toLowerCase(Locale.ROOT);
        for (String marker : List.of("/coupon/", "/store/", "/stores/", "/brand/", "/brands/", "/merchant/", "/shop/", "/shops/")) {
            int idx = lower.indexOf(marker);
            if (idx >= 0) {
                String segment = url.substring(idx + marker.length());
                int slash = segment.indexOf('/');
                if (slash >= 0) {
                    segment = segment.substring(0, slash);
                }
                int question = segment.indexOf('?');
                if (question >= 0) {
                    segment = segment.substring(0, question);
                }
                String name = slugToName(segment);
                if (isUsableBrandName(name)) {
                    return name;
                }
            }
        }

        try {
            URI uri = URI.create(url);
            String host = safe(uri.getHost()).toLowerCase(Locale.ROOT);
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            String[] parts = host.split("\\.");
            if (parts.length >= 2) {
                return slugToName(parts[0]);
            }
        } catch (Exception ignored) {
            return "";
        }

        return "";
    }

    private String queryParam(String url, String key) {
        try {
            URI uri = URI.create(url);
            String query = safe(uri.getQuery());
            if (query.isBlank()) {
                return "";
            }
            String lowerKey = key.toLowerCase(Locale.ROOT);
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String k = pair.substring(0, eq).trim().toLowerCase(Locale.ROOT);
                if (!k.equals(lowerKey)) {
                    continue;
                }
                String value = pair.substring(eq + 1).replace('+', ' ').trim();
                if (!value.isBlank()) {
                    return value;
                }
            }
        } catch (Exception ignored) {
            return "";
        }
        return "";
    }

    private Document connect(String url) throws Exception {
        return Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .header("Accept-Language", "en-US,en;q=0.9")
            .header("Cache-Control", "no-cache")
            .referrer("https://www.google.com/")
            .timeout(FETCH_TIMEOUT_MS)
            .get();
    }

    private boolean isLikelyCrawlerTarget(String rawUrl) {
        String url = safe(rawUrl).toLowerCase(Locale.ROOT);
        if (url.isBlank()) {
            return false;
        }
        return containsAny(url, "coupon", "deal", "offer", "promo", "store", "brand", "shop", "discount");
    }

    private boolean isLikelyBrandLink(String rawUrl) {
        String url = safe(rawUrl).toLowerCase(Locale.ROOT);
        return containsAny(url, "/store/", "/stores/", "/brand/", "/brands/", "/shop/", "/shops/", "/merchant/");
    }

    private boolean containsAny(String text, String... keys) {
        for (String key : keys) {
            if (text.contains(key)) {
                return true;
            }
        }
        return false;
    }

    private String deriveStoreName(CrawlerSite site) {
        String explicit = safe(site.getSiteName());
        if (!explicit.isBlank()) {
            return explicit;
        }
        try {
            URI uri = URI.create(normalizeUrl(site.getBaseUrl()));
            String host = safe(uri.getHost()).toLowerCase(Locale.ROOT);
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            String raw = host.split("\\.")[0];
            if (raw.isBlank()) {
                return "Store";
            }
            return slugToName(raw);
        } catch (Exception ex) {
            return "Store";
        }
    }

    private String resolveUrl(String baseUrl, String maybeRelative) {
        String base = normalizeUrl(baseUrl);
        String target = safe(maybeRelative);
        if (target.isBlank()) {
            return "";
        }
        if (target.startsWith("http://") || target.startsWith("https://")) {
            return normalizeUrl(target);
        }
        try {
            URI resolved = URI.create(base).resolve(target);
            return normalizeUrl(resolved.toString());
        } catch (Exception ex) {
            return "";
        }
    }

    private String normalizeUrl(String raw) {
        String value = safe(raw);
        if (value.isBlank()) {
            return "";
        }
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "https://" + value;
        }
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme() == null ? "https" : uri.getScheme();
            String host = safe(uri.getHost());
            if (host.isBlank()) {
                return "";
            }
            String path = uri.getPath() == null ? "" : uri.getPath();
            String query = uri.getQuery() == null ? "" : "?" + uri.getQuery();
            return scheme + "://" + host + path + query;
        } catch (Exception ex) {
            return "";
        }
    }

    private String slugToName(String raw) {
        String cleaned = safe(raw).replaceAll("[^A-Za-z0-9\\-_. ]", " ");
        cleaned = cleaned.replaceAll("[-_.]+", " ").replaceAll("\\s+", " ").trim();
        if (cleaned.isBlank()) {
            return "";
        }
        String[] parts = cleaned.split(" ");
        List<String> words = new ArrayList<>();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            String lower = part.toLowerCase(Locale.ROOT);
            words.add(Character.toUpperCase(lower.charAt(0)) + lower.substring(1));
        }
        return String.join(" ", words);
    }

    private String cleanName(String raw) {
        return slugToName(raw);
    }

    private boolean isDirtyCoupon(String store, String title, String code, String fallbackStore) {
        String normalizedStore = safe(store).toLowerCase(Locale.ROOT);
        String normalizedTitle = safe(title).toLowerCase(Locale.ROOT);
        String normalizedCode = safe(code).toLowerCase(Locale.ROOT);
        String normalizedFallbackStore = safe(fallbackStore).toLowerCase(Locale.ROOT);

        if (!isUsableBrandName(store)) {
            return true;
        }
        if (normalizedTitle.length() < 8 || normalizedTitle.length() > 170) {
            return true;
        }
        if (wordCount(normalizedTitle) > 24 || wordCount(normalizedStore) > 6) {
            return true;
        }
        if (containsAny(normalizedStore, STORE_NAME_BLOCKLIST.toArray(new String[0]))) {
            return true;
        }
        if (containsAny(normalizedTitle, COUPON_TITLE_BLOCKLIST.toArray(new String[0]))) {
            return true;
        }
        if (normalizedTitle.contains("coupons only") || normalizedTitle.contains("verification activity")) {
            return true;
        }
        if (normalizedTitle.contains("shop your favourite stores")
            || normalizedTitle.contains("earn cash back")
            || normalizedTitle.contains("all deals and coupons")
            || normalizedTitle.contains("copy code")
            || normalizedTitle.contains("shop now")) {
            return true;
        }
        if (normalizedTitle.contains("about this code")) {
            return true;
        }
        if ((normalizedStore.equals(normalizedFallbackStore) || normalizedStore.contains("rakuten"))
            && (normalizedTitle.contains("cash back")
            || normalizedTitle.contains("see details")
            || normalizedTitle.contains("shop now"))) {
            return true;
        }
        return normalizedCode.matches("^(featured|recommended|travel|sports|garden|gifts|financial|awesome|every|works)$");
    }

    private boolean isUsableBrandName(String raw) {
        String value = safe(raw);
        if (value.length() < 2 || value.length() > 60) {
            return false;
        }
        String lower = value.toLowerCase(Locale.ROOT);
        if (containsAny(lower, "coupon", "deal", "offer", "promo", "home", "login", "signup", "read more")) {
            return false;
        }
        return value.chars().anyMatch(Character::isLetterOrDigit);
    }

    private String normalizeCode(String raw) {
        return safe(raw).replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }

    private boolean isLikelyCode(String code) {
        if (code.isBlank()) {
            return false;
        }
        if (code.length() < 4 || code.length() > 16) {
            return false;
        }
        if (code.matches("\\d+")) {
            return false;
        }
        String lower = code.toLowerCase(Locale.ROOT);
        return !containsAny(lower, "coupon", "promo", "offer", "store");
    }

    private boolean containsDigit(String code) {
        for (int i = 0; i < code.length(); i++) {
            if (Character.isDigit(code.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private int wordCount(String text) {
        String value = safe(text);
        if (value.isBlank()) {
            return 0;
        }
        return value.split("\\s+").length;
    }

    private String capLength(String value, int maxLength) {
        String text = safe(value);
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength).trim();
    }

    private String normalizeKey(String value) {
        return safe(value).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private boolean isBlank(String value) {
        return safe(value).isBlank();
    }

    private boolean isBuiltinSite(String siteKey) {
        String key = safe(siteKey).toLowerCase(Locale.ROOT);
        return "retailmenot".equals(key) || "simplycodes".equals(key);
    }

    private boolean isAggregatorDomain(String baseUrl) {
        String url = normalizeUrl(baseUrl).toLowerCase(Locale.ROOT);
        return url.contains("rakuten.");
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

    private record BrandUpsertResult(BrandProfile profile, boolean changed) {
    }
}
