package com.couponsite.coupon;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findFirstByStoreIgnoreCaseAndTitleIgnoreCase(String store, String title);
    Optional<Coupon> findFirstByStoreIgnoreCaseAndCouponCodeIgnoreCase(String store, String couponCode);
    List<Coupon> findAllByOrderByCreatedAtDesc();
}

