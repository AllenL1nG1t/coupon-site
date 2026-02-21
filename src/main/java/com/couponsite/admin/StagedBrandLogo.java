package com.couponsite.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_staged_brand_logo_store", columnNames = {"store_name"}))
public class StagedBrandLogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String sourceSiteKey;

    @Column(nullable = false, length = 1200)
    private String sourceUrl;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] logoImage;

    @Column
    private String logoImageContentType;

    @Column(nullable = false)
    private boolean posted;

    @Column
    private LocalDateTime postedAt;

    @Column
    private Long brandProfileId;

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
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getSourceSiteKey() { return sourceSiteKey; }
    public void setSourceSiteKey(String sourceSiteKey) { this.sourceSiteKey = sourceSiteKey; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public byte[] getLogoImage() { return logoImage; }
    public void setLogoImage(byte[] logoImage) { this.logoImage = logoImage; }
    public String getLogoImageContentType() { return logoImageContentType; }
    public void setLogoImageContentType(String logoImageContentType) { this.logoImageContentType = logoImageContentType; }
    public boolean isPosted() { return posted; }
    public void setPosted(boolean posted) { this.posted = posted; }
    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
    public Long getBrandProfileId() { return brandProfileId; }
    public void setBrandProfileId(Long brandProfileId) { this.brandProfileId = brandProfileId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
