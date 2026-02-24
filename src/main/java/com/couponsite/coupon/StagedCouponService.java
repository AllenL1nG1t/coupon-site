package com.couponsite.coupon;

import com.couponsite.admin.AdminStagedCouponDto;
import com.couponsite.admin.CrawlerLogService;
import com.couponsite.brand.BrandProfileService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StagedCouponService {

    private final StagedCouponRepository stagedCouponRepository;
    private final CouponService couponService;
    private final BrandProfileService brandProfileService;
    private final CrawlerLogService crawlerLogService;

    public StagedCouponService(
        StagedCouponRepository stagedCouponRepository,
        CouponService couponService,
        BrandProfileService brandProfileService,
        CrawlerLogService crawlerLogService
    ) {
        this.stagedCouponRepository = stagedCouponRepository;
        this.couponService = couponService;
        this.brandProfileService = brandProfileService;
        this.crawlerLogService = crawlerLogService;
    }

    @Transactional
    public boolean stageFromCrawler(Coupon incoming) {
        String normalizedStore = nonBlankOrDefault(incoming.getStore(), "Unknown Store");
        String normalizedCode = nonBlankOrDefault(incoming.getCouponCode(), "SEEDEAL");

        return stagedCouponRepository.findFirstByStoreIgnoreCaseAndCouponCodeIgnoreCase(normalizedStore, normalizedCode)
            .map(existing -> {
                copyCouponFields(existing, incoming, normalizedStore, normalizedCode);
                StagedCoupon saved = stagedCouponRepository.save(existing);
                autoPostIfBrandEnabled(saved);
                return false;
            })
            .orElseGet(() -> {
                StagedCoupon staged = new StagedCoupon();
                copyCouponFields(staged, incoming, normalizedStore, normalizedCode);
                staged.setPosted(false);
                StagedCoupon saved = stagedCouponRepository.save(staged);
                autoPostIfBrandEnabled(saved);
                return true;
            });
    }

    public List<AdminStagedCouponDto> listAdminStagedCoupons() {
        return stagedCouponRepository.findAllByOrderByUpdatedAtDesc().stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional
    public AdminStagedCouponDto postToMain(Long id) {
        StagedCoupon staged = stagedCouponRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Staged coupon not found: " + id));

        return toDto(postStagedCoupon(staged));
    }

    private void autoPostIfBrandEnabled(StagedCoupon staged) {
        if (!brandProfileService.isCouponAutoPostEnabledForStore(staged.getStore())) {
            return;
        }
        try {
            postStagedCoupon(staged);
        } catch (RuntimeException ex) {
            crawlerLogService.warn(
                "[source=staged-auto-post] store=" + nonBlankOrDefault(staged.getStore(), "Unknown Store")
                    + " code=" + nonBlankOrDefault(staged.getCouponCode(), "SEEDEAL")
                    + " failed=" + ex.getClass().getSimpleName()
            );
        }
    }

    private StagedCoupon postStagedCoupon(StagedCoupon staged) {
        Coupon coupon = new Coupon();
        coupon.setStore(nonBlankOrDefault(staged.getStore(), "Unknown Store"));
        coupon.setTitle(nonBlankOrDefault(staged.getTitle(), "Untitled Offer"));
        coupon.setCategory(nonBlankOrDefault(staged.getCategory(), "other"));
        coupon.setExpires(nonBlankOrDefault(staged.getExpires(), LocalDate.now().plusDays(30).toString()));
        coupon.setCouponCode(nonBlankOrDefault(staged.getCouponCode(), "SEEDEAL"));
        coupon.setAffiliateUrl(nonBlankOrDefault(staged.getAffiliateUrl(), ""));
        coupon.setLogoUrl(nonBlankOrDefault(staged.getLogoUrl(), LogoCatalog.forStore(coupon.getStore())));
        coupon.setSource(nonBlankOrDefault(staged.getSource(), "crawler-stage"));

        CouponService.UpsertResult result = couponService.upsertWithId(coupon);
        staged.setPosted(true);
        staged.setPostedAt(LocalDateTime.now());
        staged.setPostedCouponId(result.couponId());
        return stagedCouponRepository.save(staged);
    }

    @Transactional
    public int postToMain(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int posted = 0;
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            postToMain(id);
            posted++;
        }
        return posted;
    }

    @Transactional
    public int deleteFromStaging(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<Long> validIds = ids.stream()
            .filter(id -> id != null && id > 0)
            .distinct()
            .toList();
        if (validIds.isEmpty()) {
            return 0;
        }
        int before = stagedCouponRepository.findAllById(validIds).size();
        stagedCouponRepository.deleteAllByIdInBatch(validIds);
        return before;
    }

    private void copyCouponFields(StagedCoupon target, Coupon source, String store, String code) {
        target.setStore(store);
        target.setTitle(nonBlankOrDefault(source.getTitle(), "Untitled Offer"));
        target.setCategory(nonBlankOrDefault(source.getCategory(), "other"));
        target.setExpires(nonBlankOrDefault(source.getExpires(), LocalDate.now().plusDays(30).toString()));
        target.setCouponCode(code);
        target.setAffiliateUrl(nonBlankOrDefault(source.getAffiliateUrl(), ""));
        target.setLogoUrl(nonBlankOrDefault(source.getLogoUrl(), LogoCatalog.forStore(store)));
        target.setSource(nonBlankOrDefault(source.getSource(), "crawler-stage"));
    }

    private AdminStagedCouponDto toDto(StagedCoupon coupon) {
        return new AdminStagedCouponDto(
            coupon.getId(),
            coupon.getStore(),
            coupon.getTitle(),
            coupon.getCategory(),
            coupon.getExpires(),
            coupon.getCouponCode(),
            coupon.getAffiliateUrl(),
            coupon.getLogoUrl(),
            coupon.getSource(),
            coupon.isPosted(),
            coupon.getPostedAt(),
            coupon.getPostedCouponId(),
            coupon.getCreatedAt(),
            coupon.getUpdatedAt()
        );
    }

    private String nonBlankOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
