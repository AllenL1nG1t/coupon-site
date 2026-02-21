package com.couponsite;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageRouteController {

    @GetMapping({"/about", "/about/"})
    public String about() {
        return "forward:/about.html";
    }

    @GetMapping({"/privacy", "/privacy/"})
    public String privacy() {
        return "forward:/privacy.html";
    }

    @GetMapping({"/contact", "/contact/"})
    public String contact() {
        return "forward:/contact.html";
    }

    @GetMapping({"/submit-coupon", "/submit-coupon/"})
    public String submitCoupon() {
        return "forward:/submit-coupon.html";
    }

    @GetMapping({"/affiliate-disclosure", "/affiliate-disclosure/"})
    public String affiliateDisclosure() {
        return "forward:/affiliate-disclosure.html";
    }
}
