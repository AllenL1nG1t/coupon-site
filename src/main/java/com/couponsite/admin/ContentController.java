package com.couponsite.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentSettingsService contentSettingsService;

    public ContentController(ContentSettingsService contentSettingsService) {
        this.contentSettingsService = contentSettingsService;
    }

    @GetMapping("/public")
    public ContentSettingsDto getPublicContent() {
        return contentSettingsService.get();
    }
}
