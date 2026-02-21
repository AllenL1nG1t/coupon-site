package com.couponsite.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seo")
public class SeoController {

    private final SeoSettingsService seoSettingsService;

    public SeoController(SeoSettingsService seoSettingsService) {
        this.seoSettingsService = seoSettingsService;
    }

    @GetMapping("/public")
    public SeoSettingsDto publicSeo() {
        return seoSettingsService.get();
    }
}
