package com.couponsite.admin;

import com.couponsite.blog.BlogPostDto;
import com.couponsite.blog.BlogPostUpsertRequest;
import com.couponsite.blog.BlogService;
import com.couponsite.brand.BrandProfileDto;
import com.couponsite.brand.BrandProfileService;
import com.couponsite.brand.BrandProfileUpsertRequest;
import com.couponsite.coupon.CouponService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AppSettingService appSettingService;
    private final CrawlerLogService crawlerLogService;
    private final RetailMeNotCrawlerService retailMeNotCrawlerService;
    private final SimplyCodesCrawlerService simplyCodesCrawlerService;
    private final BrandCrawlerService brandCrawlerService;
    private final BrandLogoCrawlerService brandLogoCrawlerService;
    private final GenericSiteCrawlerService genericSiteCrawlerService;
    private final CrawlerSiteService crawlerSiteService;
    private final AdSettingsService adSettingsService;
    private final ContentSettingsService contentSettingsService;
    private final CouponService couponService;
    private final BlogService blogService;
    private final BrandProfileService brandProfileService;

    public AdminController(
        AppSettingService appSettingService,
        CrawlerLogService crawlerLogService,
        RetailMeNotCrawlerService retailMeNotCrawlerService,
        SimplyCodesCrawlerService simplyCodesCrawlerService,
        BrandCrawlerService brandCrawlerService,
        BrandLogoCrawlerService brandLogoCrawlerService,
        GenericSiteCrawlerService genericSiteCrawlerService,
        CrawlerSiteService crawlerSiteService,
        AdSettingsService adSettingsService,
        ContentSettingsService contentSettingsService,
        CouponService couponService,
        BlogService blogService,
        BrandProfileService brandProfileService
    ) {
        this.appSettingService = appSettingService;
        this.crawlerLogService = crawlerLogService;
        this.retailMeNotCrawlerService = retailMeNotCrawlerService;
        this.simplyCodesCrawlerService = simplyCodesCrawlerService;
        this.brandCrawlerService = brandCrawlerService;
        this.brandLogoCrawlerService = brandLogoCrawlerService;
        this.genericSiteCrawlerService = genericSiteCrawlerService;
        this.crawlerSiteService = crawlerSiteService;
        this.adSettingsService = adSettingsService;
        this.contentSettingsService = contentSettingsService;
        this.couponService = couponService;
        this.blogService = blogService;
        this.brandProfileService = brandProfileService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardDto dashboard() {
        return new AdminDashboardDto(appSettingService.isCouponCrawlerEnabled(), crawlerLogService.latest());
    }

    @GetMapping("/settings")
    public CrawlerSettingsDto settings() {
        return new CrawlerSettingsDto(
            appSettingService.isCouponCrawlerEnabled(),
            appSettingService.isBrandCrawlerEnabled(),
            appSettingService.isBrandLogoCrawlerEnabled(),
            Math.max(1, appSettingService.getCouponCrawlerIntervalMs() / 60_000),
            Math.max(1, appSettingService.getBrandCrawlerIntervalMs() / 60_000),
            Math.max(1, appSettingService.getBrandLogoCrawlerIntervalMs() / 60_000)
        );
    }

    @PutMapping("/settings")
    public CrawlerSettingsDto updateSettings(@RequestBody CrawlerSettingsDto body) {
        boolean couponCrawlerEnabled = appSettingService.setCouponCrawlerEnabled(body.couponCrawlerEnabled());
        boolean brandCrawlerEnabled = appSettingService.setBrandCrawlerEnabled(body.brandCrawlerEnabled());
        boolean brandLogoCrawlerEnabled = appSettingService.setBrandLogoCrawlerEnabled(body.brandLogoCrawlerEnabled());
        long couponMinutes = body.couponCrawlerIntervalMinutes() <= 0 ? 30 : body.couponCrawlerIntervalMinutes();
        long brandMinutes = body.brandCrawlerIntervalMinutes() <= 0 ? 30 : body.brandCrawlerIntervalMinutes();
        long logoMinutes = body.brandLogoCrawlerIntervalMinutes() <= 0 ? 30 : body.brandLogoCrawlerIntervalMinutes();
        long couponIntervalMs = appSettingService.setCouponCrawlerIntervalMs(couponMinutes * 60_000);
        long brandIntervalMs = appSettingService.setBrandCrawlerIntervalMs(brandMinutes * 60_000);
        long logoIntervalMs = appSettingService.setBrandLogoCrawlerIntervalMs(logoMinutes * 60_000);

        return new CrawlerSettingsDto(
            couponCrawlerEnabled,
            brandCrawlerEnabled,
            brandLogoCrawlerEnabled,
            Math.max(1, couponIntervalMs / 60_000),
            Math.max(1, brandIntervalMs / 60_000),
            Math.max(1, logoIntervalMs / 60_000)
        );
    }

    @GetMapping("/logs")
    public List<CrawlerLogDto> logs() {
        return crawlerLogService.latest();
    }

    @PostMapping("/crawler/run")
    public ResponseEntity<String> runCrawler() {
        CouponRunResult couponResult = runCouponCrawlerInternal();
        int brandSeedCount = brandCrawlerService.crawlLatest();
        int brandCustomCount = genericSiteCrawlerService.crawlBrandsFromEnabledSites();
        int brandCount = brandSeedCount + brandCustomCount;
        int logoBrandCount = brandLogoCrawlerService.crawlLatest();
        int logoCustomCount = genericSiteCrawlerService.crawlLogosFromEnabledSites();
        int logoCount = logoBrandCount + logoCustomCount;
        int couponCount = couponResult.total();
        int total = couponCount + brandCount + logoCount;
        long brandProfilesTotal = brandProfileService.count();
        return ResponseEntity.ok(
            "Crawler done, coupons=" + couponCount
                + " (retailmenot=" + couponResult.retailMeNot() + ", simplycodes=" + couponResult.simplyCodes() + ", custom=" + couponResult.custom() + ")"
                + ", brandUpserts=" + brandCount
                + " (brandSeeds=" + brandSeedCount + ", custom=" + brandCustomCount + ")"
                + ", brandProfilesTotal=" + brandProfilesTotal
                + ", brandLogos=" + logoCount
                + " (brandProfiles=" + logoBrandCount + ", custom=" + logoCustomCount + ")"
                + ", totalUpserts=" + total
        );
    }

    @PostMapping("/crawler/run-coupons")
    public ResponseEntity<String> runCouponCrawler() {
        CouponRunResult couponResult = runCouponCrawlerInternal();
        return ResponseEntity.ok(
            "Coupon crawler done, total=" + couponResult.total()
                + ", retailmenot=" + couponResult.retailMeNot()
                + ", simplycodes=" + couponResult.simplyCodes()
                + ", custom=" + couponResult.custom()
        );
    }

    @PostMapping("/crawler/run-brands")
    public ResponseEntity<String> runBrandCrawler() {
        int brandSeedCount = brandCrawlerService.crawlLatest();
        int brandCustomCount = genericSiteCrawlerService.crawlBrandsFromEnabledSites();
        int brandCount = brandSeedCount + brandCustomCount;
        long brandProfilesTotal = brandProfileService.count();
        return ResponseEntity.ok(
            "Brand crawler done, upserts=" + brandCount
                + ", brandSeeds=" + brandSeedCount
                + ", custom=" + brandCustomCount
                + ", brandProfilesTotal=" + brandProfilesTotal
        );
    }

    @PostMapping("/crawler/run-brand-logos")
    public ResponseEntity<String> runBrandLogoCrawler() {
        int brandCount = brandLogoCrawlerService.crawlLatest();
        int customCount = genericSiteCrawlerService.crawlLogosFromEnabledSites();
        int logoCount = brandCount + customCount;
        return ResponseEntity.ok("Brand logo crawler done, total=" + logoCount + ", brandProfiles=" + brandCount + ", custom=" + customCount);
    }

    private CouponRunResult runCouponCrawlerInternal() {
        int retailCount = retailMeNotCrawlerService.crawlLatest();
        int simplyCodesCount = simplyCodesCrawlerService.crawlLatest();
        int customCount = genericSiteCrawlerService.crawlCouponsFromEnabledSites();
        return new CouponRunResult(retailCount, simplyCodesCount, customCount);
    }

    private record CouponRunResult(int retailMeNot, int simplyCodes, int custom) {
        int total() {
            return retailMeNot + simplyCodes + custom;
        }
    }

    @GetMapping("/crawler/sites")
    public List<CrawlerSiteDto> crawlerSites() {
        return crawlerSiteService.listAll();
    }

    @PutMapping("/crawler/sites")
    public CrawlerSiteDto upsertCrawlerSite(@RequestBody CrawlerSiteUpsertRequest request) {
        return crawlerSiteService.upsert(request);
    }

    @DeleteMapping("/crawler/sites")
    public ResponseEntity<Void> deleteCrawlerSite(@RequestParam Long id) {
        crawlerSiteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coupons")
    public List<AdminCouponDto> coupons() {
        return couponService.listAdminCoupons();
    }

    @PutMapping("/coupons")
    public AdminCouponDto upsertCoupon(@RequestBody AdminCouponUpsertRequest request) {
        return couponService.upsertFromAdmin(request);
    }

    @DeleteMapping("/coupons")
    public ResponseEntity<Void> deleteCoupon(@RequestParam Long id) {
        couponService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blogs")
    public List<BlogPostDto> blogs() {
        return blogService.listAll();
    }

    @PutMapping("/blogs")
    public BlogPostDto upsertBlog(@RequestBody BlogPostUpsertRequest request) {
        return blogService.upsert(request);
    }

    @DeleteMapping("/blogs")
    public ResponseEntity<Void> deleteBlog(@RequestParam Long id) {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/brands")
    public List<BrandProfileDto> brands() {
        return brandProfileService.listAll();
    }

    @PutMapping("/brands")
    public BrandProfileDto upsertBrand(@RequestBody BrandProfileUpsertRequest request) {
        return brandProfileService.upsert(request);
    }

    @DeleteMapping("/brands")
    public ResponseEntity<Void> deleteBrand(@RequestParam Long id) {
        brandProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ads")
    public AdSettingsDto ads() {
        return adSettingsService.get();
    }

    @PutMapping("/ads")
    public AdSettingsDto updateAds(@RequestBody AdSettingsDto request) {
        return adSettingsService.save(request);
    }

    @GetMapping("/content")
    public ContentSettingsDto content() {
        return contentSettingsService.get();
    }

    @PutMapping("/content")
    public ContentSettingsDto updateContent(@RequestBody ContentSettingsDto request) {
        return contentSettingsService.save(request);
    }

    @PostMapping("/uploads/images")
    public ResponseEntity<UploadImageResponse> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Path uploadDir = Paths.get("data", "uploads");
        Files.createDirectories(uploadDir);
        String original = file.getOriginalFilename() == null ? "image.png" : file.getOriginalFilename();
        String ext = safeExtension(original);
        String filename = UUID.randomUUID() + ext;
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return ResponseEntity.ok(new UploadImageResponse("/uploads/" + filename));
    }

    private String safeExtension(String original) {
        int i = original.lastIndexOf('.');
        if (i < 0 || i == original.length() - 1) {
            return ".png";
        }
        String ext = original.substring(i).toLowerCase();
        if (ext.matches("\\.(png|jpg|jpeg|gif|webp|svg)")) {
            return ext;
        }
        return ".png";
    }
}

