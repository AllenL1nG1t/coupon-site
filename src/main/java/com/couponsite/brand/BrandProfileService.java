package com.couponsite.brand;

import java.util.List;
import java.util.Locale;
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

        return toDto(brandProfileRepository.save(profile));
    }

    @Transactional
    public void delete(Long id) {
        brandProfileRepository.deleteById(id);
    }

    public long count() {
        return brandProfileRepository.count();
    }

    public String normalizeSlug(String raw) {
        String value = raw == null ? "brand" : raw.trim().toLowerCase(Locale.ROOT);
        value = value.replaceAll("[^a-z0-9]+", "-");
        value = value.replaceAll("^-+|-+$", "");
        return value.isBlank() ? "brand" : value;
    }

    private BrandProfileDto toDto(BrandProfile profile) {
        return new BrandProfileDto(
            profile.getId(),
            profile.getSlug(),
            profile.getStoreName(),
            profile.getTitle(),
            profile.getSummary(),
            profile.getDescription(),
            profile.getHeroImageUrl(),
            profile.getLogoUrl(),
            profile.getOfficialUrl()
        );
    }

    private String nonBlankOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
