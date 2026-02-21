package com.couponsite.admin;

import com.couponsite.brand.BrandProfile;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StagedBrandLogoService {

    private final StagedBrandLogoRepository stagedBrandLogoRepository;

    public StagedBrandLogoService(StagedBrandLogoRepository stagedBrandLogoRepository) {
        this.stagedBrandLogoRepository = stagedBrandLogoRepository;
    }

    @Transactional
    public void stageFromCrawler(BrandProfile profile, String sourceSiteKey, String sourceUrl, byte[] bytes, String contentType) {
        if (profile == null || bytes == null || bytes.length == 0) {
            return;
        }
        String store = safe(profile.getStoreName());
        if (store.isBlank()) {
            return;
        }
        StagedBrandLogo staged = stagedBrandLogoRepository.findByStoreNameIgnoreCase(store).orElseGet(StagedBrandLogo::new);
        staged.setStoreName(store);
        staged.setSourceSiteKey(nonBlank(sourceSiteKey, "brand-logo"));
        staged.setSourceUrl(nonBlank(sourceUrl, ""));
        staged.setLogoImage(bytes);
        staged.setLogoImageContentType(nonBlank(contentType, "image/png"));
        staged.setPosted(true);
        staged.setPostedAt(LocalDateTime.now());
        staged.setBrandProfileId(profile.getId());
        stagedBrandLogoRepository.save(staged);
    }

    public List<StagedBrandLogoDto> listAll() {
        return stagedBrandLogoRepository.findAllByOrderByUpdatedAtDesc().stream()
            .map(item -> new StagedBrandLogoDto(
                item.getId(),
                item.getStoreName(),
                item.getSourceSiteKey(),
                item.getSourceUrl(),
                "/api/admin/staged-brand-logos/image?id=" + item.getId(),
                item.isPosted(),
                item.getPostedAt(),
                item.getBrandProfileId(),
                item.getCreatedAt(),
                item.getUpdatedAt()
            ))
            .toList();
    }

    public StagedBrandLogo findById(Long id) {
        return stagedBrandLogoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Staged brand logo not found: " + id));
    }

    public long count() {
        return stagedBrandLogoRepository.count();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String nonBlank(String value, String fallback) {
        String v = safe(value);
        return v.isBlank() ? fallback : v;
    }
}
