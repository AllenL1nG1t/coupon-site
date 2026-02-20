package com.couponsite.coupon;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public List<CouponSummaryDto> coupons(
        @RequestParam(defaultValue = "all") String category,
        @RequestParam(defaultValue = "") String q
    ) {
        return couponService.listCoupons(category, q);
    }

    @PostMapping("/{id}/reveal")
    public ResponseEntity<CouponRevealDto> reveal(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(couponService.revealCoupon(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

