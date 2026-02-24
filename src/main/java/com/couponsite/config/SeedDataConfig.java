package com.couponsite.config;

import com.couponsite.admin.CrawlerLogService;
import com.couponsite.admin.CrawlerSiteService;
import com.couponsite.admin.AdminUser;
import com.couponsite.admin.AdminUserRepository;
import com.couponsite.blog.BlogPostUpsertRequest;
import com.couponsite.blog.BlogService;
import com.couponsite.brand.BrandProfileService;
import com.couponsite.brand.BrandProfileUpsertRequest;
import com.couponsite.coupon.Coupon;
import com.couponsite.coupon.LogoCatalog;
import com.couponsite.coupon.CouponService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner initCoupons(
        CouponService couponService,
        CrawlerLogService crawlerLogService,
        BlogService blogService,
        BrandProfileService brandProfileService,
        CrawlerSiteService crawlerSiteService,
        AdminUserRepository adminUserRepository,
        @Value("${admin.default.username:admin}") String defaultAdminUsername,
        @Value("${admin.default.password:admin123}") String defaultAdminPassword
    ) {
        return args -> {
            crawlerSiteService.ensureDefaults();
            ensureDefaultAdmin(adminUserRepository, defaultAdminUsername, defaultAdminPassword);

            if (couponService.count() > 0) {
                couponService.normalizeStoreLogos();
            } else {
                couponService.upsert(seed("Nike", "20% Off New Season Sneakers", "fashion", LocalDate.now().plusDays(7).toString(), "RUN20", "", LogoCatalog.forStore("Nike"), "seed"));
                couponService.upsert(seed("Expedia", "Save $50 on Hotels $300+", "travel", LocalDate.now().plusDays(10).toString(), "TRIP50", "", LogoCatalog.forStore("Expedia"), "seed"));
                couponService.upsert(seed("Best Buy", "Extra 15% Off Headphones", "electronics", LocalDate.now().plusDays(14).toString(), "SOUND15", "", LogoCatalog.forStore("Best Buy"), "seed"));
                couponService.upsert(seed("DoorDash", "$10 Off First 2 Orders", "food", LocalDate.now().plusDays(5).toString(), "FAST10", "", LogoCatalog.forStore("DoorDash"), "seed"));
                couponService.upsert(seed("Macy's", "30% Off Clearance + Free Shipping", "fashion", LocalDate.now().plusDays(12).toString(), "GLOW30", "", LogoCatalog.forStore("Macy's"), "seed"));
                couponService.upsert(seed("Samsung", "$100 Off Select Monitors", "electronics", LocalDate.now().plusDays(9).toString(), "VIEW100", "", LogoCatalog.forStore("Samsung"), "seed"));
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

            if (brandProfileService.count() == 0) {
                seedBrand(brandProfileService, "Nike", "Global sportswear brand known for footwear, apparel, and athlete partnerships.", "https://www.nike.com/");
                seedBrand(brandProfileService, "Expedia", "Online travel platform for flights, hotels, and vacation packages.", "https://www.expedia.com/");
                seedBrand(brandProfileService, "Best Buy", "Retailer focused on electronics, appliances, and tech services.", "https://www.bestbuy.com/");
                seedBrand(brandProfileService, "DoorDash", "Delivery platform for restaurants, groceries, and convenience stores.", "https://www.doordash.com/");
                seedBrand(brandProfileService, "Macy's", "Department store brand for fashion, beauty, and home products.", "https://www.macys.com/");
                seedBrand(brandProfileService, "Samsung", "Consumer electronics and appliance manufacturer.", "https://www.samsung.com/");
            }
        };
    }

    private void ensureDefaultAdmin(AdminUserRepository adminUserRepository, String username, String password) {
        if (adminUserRepository.count() > 0) {
            return;
        }
        AdminUser user = new AdminUser();
        user.setUsername(username);
        user.setPassword(password);
        adminUserRepository.save(user);
    }

    private void seedBrand(BrandProfileService service, String store, String summary, String officialUrl) {
        String slug = service.normalizeSlug(store);
        service.upsert(new BrandProfileUpsertRequest(
            null,
            slug,
            store,
            store + " Brand Intro",
            summary,
            summary + " This page is managed from admin and can be fully customized.",
            "/logos/default.svg",
            LogoCatalog.forStore(store),
            officialUrl,
            officialUrl,
            false
        ));
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

