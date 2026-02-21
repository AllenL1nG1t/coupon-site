package com.couponsite.admin;

import com.couponsite.brand.BrandProfile;
import com.couponsite.brand.BrandProfileService;
import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.CouponRepository;
import com.couponsite.coupon.LogoCatalog;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BrandCrawlerService {

    private static final int LOGO_MAX_BYTES = 1_500_000;
    private static final String DEFAULT_LOGO = "/logos/default.svg";
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
        if (!appSettingService.isCrawlerEnabled() || !appSettingService.isBrandCrawlerEnabled()) {
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
        crawlerLogService.info("Brand crawler started.");
        Map<String, BrandSeed> seeds = buildSeeds();
        int upserts = 0;
        int logosStored = 0;

        for (BrandSeed seed : seeds.values()) {
            try {
                BrandProfile profile = brandProfileService.findEntityByStore(seed.store())
                    .orElseGet(BrandProfile::new);
                boolean changed = applySeed(profile, seed);
                boolean logoChanged = tryStoreLogoImage(profile, seed);
                if (changed || logoChanged) {
                    brandProfileService.save(profile);
                    upserts++;
                }
                if (logoChanged) {
                    logosStored++;
                }
            } catch (Exception ex) {
                crawlerLogService.warn("Brand crawl failed for " + seed.store() + ": " + ex.getClass().getSimpleName());
            }
        }

        crawlerLogService.info("Brand crawler finished. upserts=" + upserts + ", logosStored=" + logosStored + ", scannedStores=" + seeds.size());
        return upserts;
    }

    private Map<String, BrandSeed> buildSeeds() {
        Map<String, BrandSeed> seeds = new LinkedHashMap<>();
        for (Coupon coupon : couponRepository.findAllByOrderByCreatedAtDesc()) {
            String store = clean(coupon.getStore());
            if (store.isBlank()) {
                continue;
            }
            String key = store.toLowerCase(Locale.ROOT);
            BrandSeed current = seeds.get(key);
            String logoUrl = preferLogoUrl(clean(coupon.getLogoUrl()), current == null ? "" : current.logoUrl());
            String officialUrl = chooseOfficialUrl(clean(coupon.getAffiliateUrl()), current == null ? "" : current.officialUrl());
            String domain = chooseDomain(officialUrl, current == null ? "" : current.domain());
            seeds.put(key, new BrandSeed(store, logoUrl, officialUrl, domain));
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

        return changed;
    }

    private boolean tryStoreLogoImage(BrandProfile profile, BrandSeed seed) {
        if (profile.getLogoImage() != null && profile.getLogoImage().length > 0) {
            return false;
        }

        DownloadedLogo downloaded = null;
        if (!isBlank(seed.domain())) {
            downloaded = downloadLogo("https://logo.clearbit.com/" + seed.domain());
            if (downloaded == null) {
                String faviconUrl = "https://www.google.com/s2/favicons?sz=256&domain_url=" +
                    URLEncoder.encode("https://" + seed.domain(), StandardCharsets.UTF_8);
                downloaded = downloadLogo(faviconUrl);
            }
        }

        if (downloaded == null && isRemoteImageUrl(seed.logoUrl())) {
            downloaded = downloadLogo(seed.logoUrl());
        }
        if (downloaded == null && isRemoteImageUrl(profile.getLogoUrl())) {
            downloaded = downloadLogo(profile.getLogoUrl());
        }
        if (downloaded == null && isLocalLogoPath(seed.logoUrl())) {
            downloaded = loadLocalLogo(seed.logoUrl());
        }
        if (downloaded == null && isLocalLogoPath(profile.getLogoUrl())) {
            downloaded = loadLocalLogo(profile.getLogoUrl());
        }
        if (downloaded == null) {
            return false;
        }

        profile.setLogoImage(downloaded.bytes());
        profile.setLogoImageContentType(downloaded.contentType());
        return true;
    }

    private DownloadedLogo downloadLogo(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();

            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                return null;
            }
            String contentType = clean(connection.getContentType()).toLowerCase(Locale.ROOT);
            if (!contentType.startsWith("image/") && !url.toLowerCase(Locale.ROOT).endsWith(".svg")) {
                return null;
            }

            try (InputStream in = connection.getInputStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int read;
                int total = 0;
                while ((read = in.read(buffer)) != -1) {
                    total += read;
                    if (total > LOGO_MAX_BYTES) {
                        return null;
                    }
                    out.write(buffer, 0, read);
                }
                byte[] bytes = out.toByteArray();
                if (bytes.length == 0) {
                    return null;
                }
                String normalizedType = contentType.startsWith("image/")
                    ? contentType
                    : "image/svg+xml";
                return new DownloadedLogo(bytes, normalizedType);
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private DownloadedLogo loadLocalLogo(String logoPath) {
        String path = clean(logoPath);
        if (!path.startsWith("/")) {
            return null;
        }
        try {
            byte[] bytes = null;
            try (InputStream in = BrandCrawlerService.class.getResourceAsStream("/static" + path)) {
                if (in != null) {
                    bytes = readBytes(in);
                }
            }
            if (bytes == null || bytes.length == 0) {
                Path sourcePath = Paths.get("src", "main", "resources", "static" + path);
                if (Files.exists(sourcePath)) {
                    bytes = Files.readAllBytes(sourcePath);
                }
            }
            if (bytes == null || bytes.length == 0) {
                Path compiledPath = Paths.get("target", "classes", "static" + path);
                if (Files.exists(compiledPath)) {
                    bytes = Files.readAllBytes(compiledPath);
                }
            }
            if (bytes == null || bytes.length == 0 || bytes.length > LOGO_MAX_BYTES) {
                return null;
            }
            String contentType = detectContentType(path);
            return new DownloadedLogo(bytes, contentType);
        } catch (Exception ignored) {
            return null;
        }
    }

    private byte[] readBytes(InputStream in) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            int total = 0;
            while ((read = in.read(buffer)) != -1) {
                total += read;
                if (total > LOGO_MAX_BYTES) {
                    return null;
                }
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    private String chooseOfficialUrl(String affiliateUrl, String existing) {
        String derived = toOfficialSite(affiliateUrl);
        if (!isBlank(derived)) {
            return derived;
        }
        return existing;
    }

    private String chooseDomain(String officialUrl, String existingDomain) {
        String domain = extractDomain(officialUrl);
        if (!isBlank(domain)) {
            return domain;
        }
        return existingDomain;
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

    private String extractDomain(String officialUrl) {
        try {
            if (isBlank(officialUrl)) {
                return "";
            }
            String host = clean(URI.create(officialUrl).getHost()).toLowerCase(Locale.ROOT);
            if (isBlank(host)) {
                return "";
            }
            String[] parts = host.split("\\.");
            if (parts.length >= 2) {
                return parts[parts.length - 2] + "." + parts[parts.length - 1];
            }
            return host;
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

    private boolean isRemoteImageUrl(String url) {
        String value = clean(url).toLowerCase(Locale.ROOT);
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private boolean isLocalLogoPath(String url) {
        String value = clean(url).toLowerCase(Locale.ROOT);
        return value.startsWith("/logos/");
    }

    private String detectContentType(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        return "application/octet-stream";
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

    private record DownloadedLogo(byte[] bytes, String contentType) {
    }

    private record BrandSeed(String store, String logoUrl, String officialUrl, String domain) {
    }
}
