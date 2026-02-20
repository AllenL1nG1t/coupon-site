package com.couponsite.coupon;

import com.couponsite.admin.CrawlerLogService;
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
}

