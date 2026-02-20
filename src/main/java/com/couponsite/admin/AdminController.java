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
        this.adSettingsService = adSettingsService;
        this.contentSettingsService = contentSettingsService;
        this.couponService = couponService;
        this.blogService = blogService;
        this.brandProfileService = brandProfileService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardDto dashboard() {
        return new AdminDashboardDto(appSettingService.isCrawlerEnabled(), crawlerLogService.latest());
    }

    @GetMapping("/settings")
    public CrawlerSettingsDto settings() {
        return new CrawlerSettingsDto(appSettingService.isCrawlerEnabled());
    }

    @PutMapping("/settings")
    public CrawlerSettingsDto updateSettings(@RequestBody CrawlerSettingsDto body) {
        return new CrawlerSettingsDto(appSettingService.setCrawlerEnabled(body.crawlerEnabled()));
    }

    @GetMapping("/logs")
    public List<CrawlerLogDto> logs() {
        return crawlerLogService.latest();
    }

    @PostMapping("/crawler/run")
    public ResponseEntity<String> runCrawler() {
        int retailCount = retailMeNotCrawlerService.crawlLatest();
        int simplyCodesCount = simplyCodesCrawlerService.crawlLatest();
        int total = retailCount + simplyCodesCount;
        return ResponseEntity.ok("Crawler done, retailmenot=" + retailCount + ", simplycodes=" + simplyCodesCount + ", total=" + total);
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

