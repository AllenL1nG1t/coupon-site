package com.couponsite.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

@Entity
public class CrawlerSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String siteKey;

    @Column(nullable = false, length = 255)
    private String siteName;

    @Column(nullable = false, length = 1200)
    private String baseUrl;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean couponEnabled;

    @Column(nullable = false)
    private boolean brandEnabled;

    @Column(nullable = false)
    private boolean logoEnabled;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSiteKey() { return siteKey; }
    public void setSiteKey(String siteKey) { this.siteKey = siteKey; }
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isCouponEnabled() { return couponEnabled; }
    public void setCouponEnabled(boolean couponEnabled) { this.couponEnabled = couponEnabled; }
    public boolean isBrandEnabled() { return brandEnabled; }
    public void setBrandEnabled(boolean brandEnabled) { this.brandEnabled = brandEnabled; }
    public boolean isLogoEnabled() { return logoEnabled; }
    public void setLogoEnabled(boolean logoEnabled) { this.logoEnabled = logoEnabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

