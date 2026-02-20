package com.couponsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CouponSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponSiteApplication.class, args);
    }
}

