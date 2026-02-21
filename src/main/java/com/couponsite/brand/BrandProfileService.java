package com.couponsite.brand;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrandProfileService {

    private final BrandProfileRepository brandProfileRepository;

    public BrandProfileService(BrandProfileRepository brandProfileRepository) {
        this.brandProfileRepository = brandProfileRepository;
    }

    public List<BrandProfileDto> listAll() {
        return brandProfileRepository.findAllByOrderByStoreNameAsc().stream().map(this::toDto).toList();
    }

    public BrandProfileDto findBySlug(String slug) {
        BrandProfile profile = brandProfileRepository.findBySlugIgnoreCase(slug)
            .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        return toDto(profile);
    }

    public BrandProfileDto findByStore(String store) {
        BrandProfile profile = brandProfileRepository.findByStoreNameIgnoreCase(store)
            .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        return toDto(profile);
    }

    public Optional<BrandLogoPayload> findLogoBySlug(String slug) {
        return brandProfileRepository.findBySlugIgnoreCase(slug)
            .flatMap(this::toLogoPayload);
    }

    public Optional<BrandLogoPayload> findLogoByStore(String store) {
        return brandProfileRepository.findByStoreNameIgnoreCase(store)
            .flatMap(this::toLogoPayload);
    }

    public String resolvePublicLogoUrlByStore(String store, String fallbackLogoUrl) {
        return brandProfileRepository.findByStoreNameIgnoreCase(store)
            .map(profile -> {
                if (hasStoredLogo(profile)) {
                    return "/api/brands/logo?slug=" + URLEncoder.encode(profile.getSlug(), StandardCharsets.UTF_8);
                }
                if (!isBlank(profile.getLogoUrl())) {
                    return profile.getLogoUrl();
                }
                return fallbackLogoUrl;
            })
            .orElse(fallbackLogoUrl);
    }

    @Transactional
    public BrandProfileDto upsert(BrandProfileUpsertRequest request) {
        BrandProfile profile = request.id() == null
            ? new BrandProfile()
            : brandProfileRepository.findById(request.id()).orElseThrow(() -> new IllegalArgumentException("Brand not found"));

        String store = nonBlankOrDefault(request.storeName(), "Unknown");
        String slug = normalizeSlug(nonBlankOrDefault(request.slug(), store));

        profile.setStoreName(store);
        profile.setSlug(slug);
        profile.setTitle(nonBlankOrDefault(request.title(), store));
        profile.setSummary(nonBlankOrDefault(request.summary(), ""));
        profile.setDescription(nonBlankOrDefault(request.description(), ""));
        profile.setHeroImageUrl(nonBlankOrDefault(request.heroImageUrl(), "/logos/default.svg"));
        profile.setLogoUrl(nonBlankOrDefault(request.logoUrl(), "/logos/default.svg"));
        profile.setOfficialUrl(nonBlankOrDefault(request.officialUrl(), "https://example.com"));
        profile.setAffiliateUrl(nonBlankOrDefault(request.affiliateUrl(), profile.getOfficialUrl()));

        return toDto(brandProfileRepository.save(profile));
    }

    @Transactional
    public void delete(Long id) {
        brandProfileRepository.deleteById(id);
    }

    public long count() {
        return brandProfileRepository.count();
    }

    @Transactional
    public BrandProfile save(BrandProfile profile) {
        return brandProfileRepository.save(profile);
    }

    public Optional<BrandProfile> findEntityByStore(String store) {
        return brandProfileRepository.findByStoreNameIgnoreCase(store);
    }

    public List<BrandProfile> findAllEntities() {
        return brandProfileRepository.findAll();
    }

    public String normalizeSlug(String raw) {
        String value = raw == null ? "brand" : raw.trim().toLowerCase(Locale.ROOT);
        value = value.replaceAll("[^a-z0-9]+", "-");
        value = value.replaceAll("^-+|-+$", "");
        return value.isBlank() ? "brand" : value;
    }

    private BrandProfileDto toDto(BrandProfile profile) {
        String logoUrl = hasStoredLogo(profile)
            ? "/api/brands/logo?slug=" + URLEncoder.encode(profile.getSlug(), StandardCharsets.UTF_8)
            : profile.getLogoUrl();
        return new BrandProfileDto(
            profile.getId(),
            profile.getSlug(),
            profile.getStoreName(),
            profile.getTitle(),
            profile.getSummary(),
            profile.getDescription(),
            profile.getHeroImageUrl(),
            logoUrl,
            profile.getOfficialUrl(),
            nonBlankOrDefault(profile.getAffiliateUrl(), profile.getOfficialUrl()),
            profile.getCreatedAt(),
            profile.getUpdatedAt()
        );
    }

    private Optional<BrandLogoPayload> toLogoPayload(BrandProfile profile) {
        if (!hasStoredLogo(profile)) {
            return Optional.empty();
        }
        String contentType = nonBlankOrDefault(profile.getLogoImageContentType(), "image/png");
        return Optional.of(new BrandLogoPayload(profile.getLogoImage(), contentType));
    }

    private boolean hasStoredLogo(BrandProfile profile) {
        return profile.getLogoImage() != null && profile.getLogoImage().length > 0;
    }

    private String nonBlankOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record BrandLogoPayload(byte[] bytes, String contentType) {
    }
}
