package com.couponsite.brand;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/brands")
public class BrandProfileController {

    private final BrandProfileService brandProfileService;

    public BrandProfileController(BrandProfileService brandProfileService) {
        this.brandProfileService = brandProfileService;
    }

    @GetMapping
    public List<BrandProfileDto> list() {
        return brandProfileService.listAll();
    }

    @GetMapping("/detail")
    public ResponseEntity<BrandProfileDto> detailBySlug(@RequestParam String slug) {
        try {
            return ResponseEntity.ok(brandProfileService.findBySlug(slug));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-store")
    public ResponseEntity<BrandProfileDto> detailByStore(@RequestParam String store) {
        try {
            return ResponseEntity.ok(brandProfileService.findByStore(store));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
