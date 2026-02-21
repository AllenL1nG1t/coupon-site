package com.couponsite.admin;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StagedBrandLogoRepository extends JpaRepository<StagedBrandLogo, Long> {
    Optional<StagedBrandLogo> findByStoreNameIgnoreCase(String storeName);
    List<StagedBrandLogo> findAllByOrderByUpdatedAtDesc();
}
