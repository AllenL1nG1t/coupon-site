package com.couponsite.config;

import com.couponsite.admin.CrawlerLogService;
import com.couponsite.blog.BlogPostUpsertRequest;
import com.couponsite.blog.BlogService;
import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.LogoCatalog;
import com.couponsite.coupon.CouponService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner initCoupons(CouponService couponService, CrawlerLogService crawlerLogService, BlogService blogService) {
        return args -> {
            if (couponService.count() > 0) {
                couponService.normalizeStoreLogos();
            } else {
                couponService.upsert(seed("Nike", "20% Off New Season Sneakers", "fashion", "Ends tonight", "RUN20", "https://example-affiliate.com/nike?cid=dealnest", LogoCatalog.forStore("Nike"), "seed"));
                couponService.upsert(seed("Expedia", "Save $50 on Hotels $300+", "travel", "2 days left", "TRIP50", "https://example-affiliate.com/expedia?cid=dealnest", LogoCatalog.forStore("Expedia"), "seed"));
                couponService.upsert(seed("Best Buy", "Extra 15% Off Headphones", "electronics", "This week", "SOUND15", "https://example-affiliate.com/bestbuy?cid=dealnest", LogoCatalog.forStore("Best Buy"), "seed"));
                couponService.upsert(seed("DoorDash", "$10 Off First 2 Orders", "food", "No expiration date", "FAST10", "https://example-affiliate.com/doordash?cid=dealnest", LogoCatalog.forStore("DoorDash"), "seed"));
                couponService.upsert(seed("Macy's", "30% Off Clearance + Free Shipping", "fashion", "Ends Sunday", "GLOW30", "https://example-affiliate.com/macys?cid=dealnest", LogoCatalog.forStore("Macy's"), "seed"));
                couponService.upsert(seed("Samsung", "$100 Off Select Monitors", "electronics", "Limited stock", "VIEW100", "https://example-affiliate.com/samsung?cid=dealnest", LogoCatalog.forStore("Samsung"), "seed"));
                crawlerLogService.info("Seed coupons initialized.");
            }

            couponService.normalizeStoreLogos();

            if (blogService.count() == 0) {
                blogService.upsert(new BlogPostUpsertRequest(
                    null,
                    "How to Stack Coupons With Cashback in 2026",
                    "Simple stacking strategy: promo code + cashback portal + card offer.",
                    "Use verified promo codes first, then activate cashback, and finally check card-linked offers before checkout.",
                    "/logos/default.svg",
                    true
                ));
                blogService.upsert(new BlogPostUpsertRequest(
                    null,
                    "Top Mistakes That Make Coupon Codes Fail",
                    "Avoid these common errors when applying store promotions.",
                    "Most failures come from regional restrictions, minimum order thresholds, and code exclusions on discounted items.",
                    "/logos/default.svg",
                    true
                ));
            }
        };
    }

    private Coupon seed(
        String store,
        String title,
        String category,
        String expires,
        String code,
        String affiliateUrl,
        String logoUrl,
        String source
    ) {
        Coupon coupon = new Coupon();
        coupon.setStore(store);
        coupon.setTitle(title);
        coupon.setCategory(category);
        coupon.setExpires(expires);
        coupon.setCouponCode(code);
        coupon.setAffiliateUrl(affiliateUrl);
        coupon.setLogoUrl(logoUrl);
        coupon.setSource(source);
        return coupon;
    }
}

