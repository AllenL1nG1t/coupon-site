package com.couponsite.coupon;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StagedCouponRepository extends JpaRepository<StagedCoupon, Long> {
    Optional<StagedCoupon> findFirstByStoreIgnoreCaseAndCouponCodeIgnoreCase(String store, String couponCode);
    List<StagedCoupon> findAllByOrderByUpdatedAtDesc();
}
