package com.couponsite.coupon;

import com.couponsite.admin.CrawlerLogService;
import com.couponsite.admin.AdminCouponDto;
import com.couponsite.admin.AdminCouponUpsertRequest;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CrawlerLogService crawlerLogService;

    public CouponService(CouponRepository couponRepository, CrawlerLogService crawlerLogService) {
        this.couponRepository = couponRepository;
        this.crawlerLogService = crawlerLogService;
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
            .map(coupon -> new CouponSummaryDto(
                coupon.getId(),
                coupon.getStore(),
                coupon.getTitle(),
                coupon.getCategory(),
                coupon.getExpires(),
                coupon.getLogoUrl(),
                coupon.getSource()
            ))
            .toList();
    }

    @Transactional
    public CouponRevealDto revealCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Coupon not found: " + id));

        crawlerLogService.info("Coupon revealed: id=" + id + ", store=" + coupon.getStore());
        return new CouponRevealDto(coupon.getId(), coupon.getCouponCode(), coupon.getAffiliateUrl());
    }

    @Transactional
    public void upsert(Coupon incoming) {
        couponRepository.findFirstByStoreIgnoreCaseAndTitleIgnoreCase(incoming.getStore(), incoming.getTitle())
            .ifPresentOrElse(existing -> {
                existing.setCategory(incoming.getCategory());
                existing.setExpires(incoming.getExpires());
                existing.setCouponCode(incoming.getCouponCode());
                existing.setAffiliateUrl(incoming.getAffiliateUrl());
                existing.setLogoUrl(incoming.getLogoUrl());
                existing.setSource(incoming.getSource());
                couponRepository.save(existing);
            }, () -> couponRepository.save(incoming));
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
                coupon.getSource()
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
        coupon.setAffiliateUrl(nonBlankOrDefault(request.affiliateUrl(), "https://example-affiliate.com"));
        coupon.setLogoUrl(nonBlankOrDefault(request.logoUrl(), LogoCatalog.forStore(coupon.getStore())));
        coupon.setSource(nonBlankOrDefault(request.source(), "admin"));
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
            saved.getSource()
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
}

