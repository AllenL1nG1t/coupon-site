package com.couponsite.coupon;

import com.couponsite.admin.CrawlerLogService;
import com.couponsite.admin.AdminCouponDto;
import com.couponsite.admin.AdminCouponUpsertRequest;
import com.couponsite.brand.BrandProfileService;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CrawlerLogService crawlerLogService;
    private final BrandProfileService brandProfileService;

    public CouponService(
        CouponRepository couponRepository,
        CrawlerLogService crawlerLogService,
        BrandProfileService brandProfileService
    ) {
        this.couponRepository = couponRepository;
        this.crawlerLogService = crawlerLogService;
        this.brandProfileService = brandProfileService;
    }

    public List<CouponSummaryDto> listCoupons(String category, String query) {
        String normalizedCategory = category == null ? "all" : category.trim().toLowerCase(Locale.ROOT);
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        return couponRepository.findAll().stream()
            .filter(coupon -> "all".equals(normalizedCategory) || coupon.getCategory().equalsIgnoreCase(normalizedCategory))
            .filter(coupon -> {
                if (normalizedQuery.isBlank()) {
                    return true;
                }
                String merged = (coupon.getStore() + " " + coupon.getTitle() + " " + coupon.getCategory()).toLowerCase(Locale.ROOT);
                return merged.contains(normalizedQuery);
            })
            .map(this::toSummary)
            .toList();
    }

    public List<CouponSummaryDto> listCouponsByStore(String store) {
        if (store == null || store.isBlank()) {
            return List.of();
        }
        String target = store.trim();
        List<Coupon> exact = couponRepository.findAllByStoreIgnoreCaseOrderByCreatedAtDesc(target);
        if (!exact.isEmpty()) {
            return exact.stream().map(this::toSummary).toList();
        }

        String normalizedTarget = normalizeStoreKey(target);
        return couponRepository.findAllByOrderByCreatedAtDesc().stream()
            .filter(coupon -> {
                String normalizedStore = normalizeStoreKey(coupon.getStore());
                return normalizedStore.contains(normalizedTarget) || normalizedTarget.contains(normalizedStore);
            })
            .map(this::toSummary)
            .toList();
    }

    @Transactional
    public CouponRevealDto revealCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Coupon not found: " + id));

        coupon.setClickCount(coupon.getClickCount() + 1);
        crawlerLogService.info("Coupon revealed: id=" + id + ", store=" + coupon.getStore());
        String affiliateUrl = resolveAffiliateUrl(coupon);
        return new CouponRevealDto(coupon.getId(), coupon.getCouponCode(), affiliateUrl);
    }

    @Transactional
    public boolean upsert(Coupon incoming) {
        String normalizedStore = nonBlankOrDefault(incoming.getStore(), "Unknown Store");
        String normalizedCode = nonBlankOrDefault(incoming.getCouponCode(), "SEEDEAL");

        incoming.setStore(normalizedStore);
        incoming.setCouponCode(normalizedCode);

        return couponRepository.findFirstByStoreIgnoreCaseAndCouponCodeIgnoreCase(normalizedStore, normalizedCode)
            .map(existing -> {
                existing.setTitle(incoming.getTitle());
                existing.setCategory(incoming.getCategory());
                existing.setExpires(incoming.getExpires());
                existing.setAffiliateUrl(incoming.getAffiliateUrl());
                existing.setLogoUrl(incoming.getLogoUrl());
                existing.setSource(incoming.getSource());
                if (existing.getClickCount() == null) {
                    existing.setClickCount(0);
                }
                couponRepository.save(existing);
                return false;
            })
            .orElseGet(() -> {
                if (incoming.getClickCount() == null) {
                    incoming.setClickCount(0);
                }
                couponRepository.save(incoming);
                return true;
            });
    }

    public long count() {
        return couponRepository.count();
    }

    @Transactional
    public void normalizeStoreLogos() {
        couponRepository.findAll().forEach(coupon -> {
            String expected = LogoCatalog.forStore(coupon.getStore());
            if (!expected.equals(coupon.getLogoUrl())) {
                coupon.setLogoUrl(expected);
                couponRepository.save(coupon);
            }
        });
    }

    public List<AdminCouponDto> listAdminCoupons() {
        return couponRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(coupon -> new AdminCouponDto(
                coupon.getId(),
                coupon.getStore(),
                coupon.getTitle(),
                coupon.getCategory(),
                coupon.getExpires(),
                coupon.getCouponCode(),
                coupon.getAffiliateUrl(),
                coupon.getLogoUrl(),
                coupon.getSource(),
                coupon.getClickCount()
            )).toList();
    }

    @Transactional
    public AdminCouponDto upsertFromAdmin(AdminCouponUpsertRequest request) {
        Coupon coupon = request.id() == null
            ? new Coupon()
            : couponRepository.findById(request.id()).orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        coupon.setStore(nonBlankOrDefault(request.store(), "Unknown Store"));
        coupon.setTitle(nonBlankOrDefault(request.title(), "Untitled Offer"));
        coupon.setCategory(nonBlankOrDefault(request.category(), "other"));
        coupon.setExpires(nonBlankOrDefault(request.expires(), "Limited time"));
        coupon.setCouponCode(nonBlankOrDefault(request.couponCode(), "SEEDEAL"));
        coupon.setAffiliateUrl(nonBlankOrDefault(request.affiliateUrl(), ""));
        coupon.setLogoUrl(nonBlankOrDefault(request.logoUrl(), LogoCatalog.forStore(coupon.getStore())));
        coupon.setSource(nonBlankOrDefault(request.source(), "admin"));
        if (request.clickCount() != null && request.clickCount() >= 0) {
            coupon.setClickCount(request.clickCount());
        } else if (coupon.getClickCount() == null) {
            coupon.setClickCount(0);
        }
        Coupon saved = couponRepository.save(coupon);

        return new AdminCouponDto(
            saved.getId(),
            saved.getStore(),
            saved.getTitle(),
            saved.getCategory(),
            saved.getExpires(),
            saved.getCouponCode(),
            saved.getAffiliateUrl(),
            saved.getLogoUrl(),
            saved.getSource(),
            saved.getClickCount()
        );
    }

    @Transactional
    public void deleteById(Long id) {
        couponRepository.deleteById(id);
    }

    private String nonBlankOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private CouponSummaryDto toSummary(Coupon coupon) {
        return new CouponSummaryDto(
            coupon.getId(),
            coupon.getStore(),
            coupon.getTitle(),
            coupon.getCategory(),
            coupon.getExpires(),
            coupon.getLogoUrl(),
            coupon.getSource(),
            coupon.getClickCount()
        );
    }

    private String normalizeStoreKey(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private String resolveAffiliateUrl(Coupon coupon) {
        if (coupon.getAffiliateUrl() != null && !coupon.getAffiliateUrl().isBlank()) {
            return coupon.getAffiliateUrl();
        }
        return brandProfileService.findEntityByStore(coupon.getStore())
            .map(profile -> {
                if (profile.getAffiliateUrl() != null && !profile.getAffiliateUrl().isBlank()) {
                    return profile.getAffiliateUrl();
                }
                if (profile.getOfficialUrl() != null && !profile.getOfficialUrl().isBlank()) {
                    return profile.getOfficialUrl();
                }
                return "https://example.com";
            })
            .orElse("https://example.com");
    }
}

