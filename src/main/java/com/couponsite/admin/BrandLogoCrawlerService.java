package com.couponsite.admin;

import com.couponsite.brand.BrandProfile;
import com.couponsite.brand.BrandProfileService;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BrandLogoCrawlerService {

    private static final int LOGO_MAX_BYTES = 1_500_000;

    private final BrandProfileService brandProfileService;
    private final AppSettingService appSettingService;
    private final CrawlerLogService crawlerLogService;
    private final StagedBrandLogoService stagedBrandLogoService;
    private final AtomicLong lastScheduledRunAt = new AtomicLong(0L);

    public BrandLogoCrawlerService(
        BrandProfileService brandProfileService,
        AppSettingService appSettingService,
        CrawlerLogService crawlerLogService,
        StagedBrandLogoService stagedBrandLogoService
    ) {
        this.brandProfileService = brandProfileService;
        this.appSettingService = appSettingService;
        this.crawlerLogService = crawlerLogService;
        this.stagedBrandLogoService = stagedBrandLogoService;
    }

    @Scheduled(fixedDelay = 30_000)
    public void scheduledRun() {
        if (!appSettingService.isBrandLogoCrawlerEnabled()) {
            return;
        }
        if (!appSettingService.isRunWindowOpen(
            appSettingService.getBrandLogoCrawlerRunAt(),
            appSettingService.getBrandLogoCrawlerLastRunAt()
        )) {
            return;
        }
        long now = System.currentTimeMillis();
        long intervalMs = appSettingService.getBrandLogoCrawlerIntervalMs();
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
        crawlerLogService.info("[source=brand-logo] Brand logo crawler started.");
        int updated = 0;
        int scannedStores = 0;

        for (BrandProfile profile : brandProfileService.findAllEntities()) {
            scannedStores++;
            try {
                if (crawlSingle(profile)) {
                    updated++;
                }
            } catch (Exception ex) {
                crawlerLogService.warn("[source=brand-logo] store=" + safe(profile.getStoreName()) + " failed=" + ex.getClass().getSimpleName());
            }
        }

        crawlerLogService.info("[source=brand-logo] Brand logo crawler finished. scannedStores=" + scannedStores + ", logosStored=" + updated);
        appSettingService.markBrandLogoCrawlerLastRunNow();
        return updated;
    }

    public boolean crawlSingle(BrandProfile profile) {
        if (profile == null || hasUsableLogo(profile)) {
            return false;
        }
        DownloadedLogo downloaded = resolveLogo(profile);
        if (downloaded == null) {
            return false;
        }
        profile.setLogoImage(downloaded.bytes());
        profile.setLogoImageContentType(downloaded.contentType());
        brandProfileService.save(profile);
        stagedBrandLogoService.stageFromCrawler(profile, "brand-logo", downloaded.sourceUrl(), downloaded.bytes(), downloaded.contentType());
        return true;
    }

    private DownloadedLogo resolveLogo(BrandProfile profile) {
        String domain = extractDomain(profile.getOfficialUrl(), profile.getStoreName());
        DownloadedLogo downloaded = null;
        if (!domain.isBlank()) {
            downloaded = downloadLogo("https://logo.clearbit.com/" + domain);
            if (downloaded == null) {
                downloaded = downloadLogo("https://api.faviconkit.com/" + domain + "/256");
            }
            if (downloaded == null) {
                downloaded = downloadLogo("https://icons.duckduckgo.com/ip3/" + domain + ".ico");
            }
            if (downloaded == null) {
                String faviconUrl = "https://www.google.com/s2/favicons?sz=256&domain_url=" +
                    URLEncoder.encode("https://" + domain, StandardCharsets.UTF_8);
                downloaded = downloadLogo(faviconUrl);
            }
        }

        if (downloaded == null) {
            downloaded = downloadFromOfficialSite(profile.getOfficialUrl());
        }

        if (downloaded == null && isRemoteImageUrl(profile.getLogoUrl())) {
                downloaded = downloadLogo(profile.getLogoUrl());
            }
        if (downloaded == null && isLocalLogoPath(profile.getLogoUrl())) {
            downloaded = loadLocalLogo(profile.getLogoUrl());
        }
        return downloaded;
    }

    private DownloadedLogo downloadFromOfficialSite(String officialUrl) {
        String url = safe(officialUrl);
        if (url.isBlank()) {
            return null;
        }
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(8000)
                .get();
            for (Element link : doc.select("link[rel~=(?i)^(icon|shortcut icon|apple-touch-icon)]")) {
                String href = link.absUrl("href");
                if (href == null || href.isBlank()) {
                    href = link.attr("href");
                }
                if (href == null || href.isBlank()) {
                    continue;
                }
                if (href.startsWith("/")) {
                    URI base = URI.create(url);
                    href = base.getScheme() + "://" + base.getHost() + href;
                }
                DownloadedLogo candidate = downloadLogo(href);
                if (candidate != null) {
                    return candidate;
                }
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
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
            String contentType = safe(connection.getContentType()).toLowerCase(Locale.ROOT);
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
                return new DownloadedLogo(bytes, normalizedType, url);
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private DownloadedLogo loadLocalLogo(String logoPath) {
        String path = safe(logoPath);
        if (!path.startsWith("/")) {
            return null;
        }
        try {
            byte[] bytes = null;
            try (InputStream in = BrandLogoCrawlerService.class.getResourceAsStream("/static" + path)) {
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
            return new DownloadedLogo(bytes, detectContentType(path), path);
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

    private String extractDomain(String officialUrl, String storeName) {
        try {
            String input = safe(officialUrl);
            if (input.isBlank() && !safe(storeName).isBlank()) {
                String guess = safe(storeName).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "");
                if (!guess.isBlank()) {
                    return guess + ".com";
                }
            }
            String host = safe(URI.create(input).getHost()).toLowerCase(Locale.ROOT);
            if (host.isBlank()) {
                return "";
            }
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            String[] parts = host.split("\\.");
            if (parts.length >= 3 && isSecondLevelSuffix(parts[parts.length - 2])) {
                return parts[parts.length - 3] + "." + parts[parts.length - 2] + "." + parts[parts.length - 1];
            }
            if (parts.length >= 2) {
                return parts[parts.length - 2] + "." + parts[parts.length - 1];
            }
            return host;
        } catch (Exception ex) {
            return "";
        }
    }

    private boolean isSecondLevelSuffix(String part) {
        return List.of("co", "com", "org", "net", "gov", "edu").contains(part);
    }

    private boolean isRemoteImageUrl(String url) {
        String value = safe(url).toLowerCase(Locale.ROOT);
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private boolean isLocalLogoPath(String url) {
        return safe(url).toLowerCase(Locale.ROOT).startsWith("/logos/");
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

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean hasUsableLogo(BrandProfile profile) {
        return profile.getLogoImage() != null && profile.getLogoImage().length > 0;
    }

    private record DownloadedLogo(byte[] bytes, String contentType, String sourceUrl) {
    }
}
