package com.couponsite.brand;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

@Entity
public class BrandProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, unique = true)
    private String storeName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String summary;

    @Column(nullable = false, length = 8000)
    private String description;

    @Column(nullable = false)
    private String heroImageUrl;

    @Column(nullable = false)
    private String logoUrl;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] logoImage;

    @Column
    private String logoImageContentType;

    @Column(nullable = false)
    private String officialUrl;

    @Column(length = 1200)
    private String affiliateUrl;

    @Column(nullable = false)
    private boolean autoPostCoupons;

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
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getHeroImageUrl() { return heroImageUrl; }
    public void setHeroImageUrl(String heroImageUrl) { this.heroImageUrl = heroImageUrl; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public byte[] getLogoImage() { return logoImage; }
    public void setLogoImage(byte[] logoImage) { this.logoImage = logoImage; }
    public String getLogoImageContentType() { return logoImageContentType; }
    public void setLogoImageContentType(String logoImageContentType) { this.logoImageContentType = logoImageContentType; }
    public String getOfficialUrl() { return officialUrl; }
    public void setOfficialUrl(String officialUrl) { this.officialUrl = officialUrl; }
    public String getAffiliateUrl() { return affiliateUrl; }
    public void setAffiliateUrl(String affiliateUrl) { this.affiliateUrl = affiliateUrl; }
    public boolean isAutoPostCoupons() { return autoPostCoupons; }
    public void setAutoPostCoupons(boolean autoPostCoupons) { this.autoPostCoupons = autoPostCoupons; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
