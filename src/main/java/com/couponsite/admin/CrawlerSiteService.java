package com.couponsite.admin;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrawlerSiteService {

    private final CrawlerSiteRepository crawlerSiteRepository;

    public CrawlerSiteService(CrawlerSiteRepository crawlerSiteRepository) {
        this.crawlerSiteRepository = crawlerSiteRepository;
    }

    public List<CrawlerSiteDto> listAll() {
        ensureDefaults();
        return crawlerSiteRepository.findAllByOrderByIdAsc().stream().map(this::toDto).toList();
    }

    public boolean isEnabled(String siteKey, DataType type) {
        if (siteKey == null || siteKey.isBlank()) {
            return false;
        }
        Optional<CrawlerSite> siteOptional = crawlerSiteRepository.findBySiteKeyIgnoreCase(siteKey.trim());
        if (siteOptional.isEmpty()) {
            return false;
        }
        CrawlerSite site = siteOptional.get();
        if (!site.isActive()) {
            return false;
        }
        return switch (type) {
            case COUPON -> site.isCouponEnabled();
            case BRAND -> site.isBrandEnabled();
            case LOGO -> site.isLogoEnabled();
        };
    }

    @Transactional
    public CrawlerSiteDto upsert(CrawlerSiteUpsertRequest request) {
        String baseUrl = normalizeUrl(request.baseUrl());
        String siteKey = deriveSiteKey(baseUrl);
        String siteName = normalizeSiteName(request.siteName(), siteKey);
        CrawlerSite site = request.id() == null
            ? crawlerSiteRepository.findBySiteKeyIgnoreCase(siteKey).orElseGet(CrawlerSite::new)
            : crawlerSiteRepository.findById(request.id()).orElseThrow(() -> new IllegalArgumentException("Crawler site not found"));

        site.setSiteKey(siteKey);
        site.setSiteName(siteName);
        site.setBaseUrl(baseUrl);
        site.setActive(request.active() == null ? true : request.active());
        site.setCouponEnabled(request.couponEnabled() == null ? true : request.couponEnabled());
        site.setBrandEnabled(request.brandEnabled() == null ? true : request.brandEnabled());
        site.setLogoEnabled(request.logoEnabled() == null ? true : request.logoEnabled());
        return toDto(crawlerSiteRepository.save(site));
    }

    @Transactional
    public void delete(Long id) {
        crawlerSiteRepository.deleteById(id);
    }

    public List<CrawlerSite> listEntities() {
        ensureDefaults();
        return crawlerSiteRepository.findAllByOrderByIdAsc();
    }

    @Transactional
    public void ensureDefaults() {
        ensureDefault("retailmenot", "RetailMeNot", "https://www.retailmenot.com/", true, false, false);
        ensureDefault("simplycodes", "SimplyCodes", "https://simplycodes.com/", true, false, false);
    }

    private void ensureDefault(
        String key,
        String name,
        String baseUrl,
        boolean couponEnabled,
        boolean brandEnabled,
        boolean logoEnabled
    ) {
        if (crawlerSiteRepository.findBySiteKeyIgnoreCase(key).isPresent()) {
            return;
        }
        CrawlerSite site = new CrawlerSite();
        site.setSiteKey(key);
        site.setSiteName(name);
        site.setBaseUrl(baseUrl);
        site.setActive(true);
        site.setCouponEnabled(couponEnabled);
        site.setBrandEnabled(brandEnabled);
        site.setLogoEnabled(logoEnabled);
        try {
            crawlerSiteRepository.save(site);
        } catch (DataIntegrityViolationException ignored) {
            // Another initializer or legacy data already inserted the same site key.
        }
    }

    private CrawlerSiteDto toDto(CrawlerSite site) {
        return new CrawlerSiteDto(
            site.getId(),
            site.getSiteKey(),
            site.getSiteName(),
            site.getBaseUrl(),
            site.isActive(),
            site.isCouponEnabled(),
            site.isBrandEnabled(),
            site.isLogoEnabled(),
            site.getCreatedAt(),
            site.getUpdatedAt()
        );
    }

    private String normalizeUrl(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("baseUrl is required");
        }
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "https://" + value;
        }
        URI uri = URI.create(value);
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Invalid baseUrl");
        }
        return uri.getScheme() + "://" + host.toLowerCase(Locale.ROOT) + "/";
    }

    private String deriveSiteKey(String baseUrl) {
        URI uri = URI.create(baseUrl);
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        if (host.startsWith("www.")) {
            host = host.substring(4);
        }
        String[] parts = host.split("\\.");
        String seed = parts.length == 0 ? host : parts[0];
        String key = seed.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        if (key.isBlank()) {
            key = "site";
        }
        return key;
    }

    private String normalizeSiteName(String name, String key) {
        String raw = name == null ? "" : name.trim();
        if (!raw.isBlank()) {
            return raw;
        }
        if (key.isBlank()) {
            return "Site";
        }
        return Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }

    public enum DataType {
        COUPON,
        BRAND,
        LOGO
    }
}
