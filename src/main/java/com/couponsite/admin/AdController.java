package com.couponsite.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    private final AdSettingsService adSettingsService;

    public AdController(AdSettingsService adSettingsService) {
        this.adSettingsService = adSettingsService;
    }

    @GetMapping("/public")
    public AdSettingsDto getPublicSettings() {
        return adSettingsService.get();
    }
}
