package com.couponsite.brand;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandProfileRepository extends JpaRepository<BrandProfile, Long> {
    Optional<BrandProfile> findBySlugIgnoreCase(String slug);
    Optional<BrandProfile> findByStoreNameIgnoreCase(String storeName);
    List<BrandProfile> findAllByOrderByStoreNameAsc();
}
