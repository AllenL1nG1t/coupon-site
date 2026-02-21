-- Dotiki Coupon MySQL initialization script
-- Keep this file in sync with JPA entities when schema changes.

CREATE DATABASE IF NOT EXISTS coupon_site
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE coupon_site;

CREATE TABLE IF NOT EXISTS app_setting (
  setting_key VARCHAR(255) NOT NULL,
  setting_value VARCHAR(255) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS blog_post (
  id BIGINT NOT NULL AUTO_INCREMENT,
  content VARCHAR(12000) NOT NULL,
  cover_image_url VARCHAR(255) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  published BIT(1) NOT NULL,
  summary VARCHAR(1600) NOT NULL,
  title VARCHAR(255) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS brand_profile (
  id BIGINT NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6) NOT NULL,
  description VARCHAR(8000) NOT NULL,
  hero_image_url VARCHAR(255) NOT NULL,
  logo_url VARCHAR(255) NOT NULL,
  logo_image LONGBLOB,
  logo_image_content_type VARCHAR(255),
  official_url VARCHAR(255) NOT NULL,
  affiliate_url VARCHAR(1200),
  slug VARCHAR(255) NOT NULL,
  store_name VARCHAR(255) NOT NULL,
  summary VARCHAR(1000) NOT NULL,
  title VARCHAR(255) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_brand_profile_slug (slug),
  UNIQUE KEY uk_brand_profile_store_name (store_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS coupon (
  id BIGINT NOT NULL AUTO_INCREMENT,
  affiliate_url VARCHAR(1200) NOT NULL,
  category VARCHAR(255) NOT NULL,
  click_count INT DEFAULT NULL,
  coupon_code VARCHAR(255) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  expires VARCHAR(255) NOT NULL,
  logo_url VARCHAR(255) NOT NULL,
  source VARCHAR(255) NOT NULL,
  store VARCHAR(255) NOT NULL,
  title VARCHAR(255) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS crawler_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6) NOT NULL,
  level VARCHAR(255) NOT NULL,
  message VARCHAR(2000) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS crawler_site (
  id BIGINT NOT NULL AUTO_INCREMENT,
  site_key VARCHAR(120) NOT NULL,
  site_name VARCHAR(255) NOT NULL,
  base_url VARCHAR(1200) NOT NULL,
  active BIT(1) NOT NULL,
  coupon_enabled BIT(1) NOT NULL,
  brand_enabled BIT(1) NOT NULL,
  logo_enabled BIT(1) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_crawler_site_site_key (site_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS admin_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO admin_user (username, password, created_at, updated_at)
SELECT 'admin', 'admin123', NOW(6), NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM admin_user WHERE LOWER(username) = 'admin'
);

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'crawler.coupon.enabled', 'false', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'crawler.coupon.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'crawler.brand.enabled', 'true', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'crawler.brand.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'crawler.brand-logo.enabled', 'true', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'crawler.brand-logo.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'crawler.coupon.interval-ms', '1800000', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'crawler.coupon.interval-ms');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'crawler.brand.interval-ms', '1800000', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'crawler.brand.interval-ms');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'crawler.brand-logo.interval-ms', '1800000', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'crawler.brand-logo.interval-ms');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.tagline', 'Deals are user-submitted and manually reviewed.', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.tagline');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.twitterUrl', 'https://twitter.com/', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.twitterUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.instagramUrl', 'https://instagram.com/', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.instagramUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.facebookUrl', 'https://facebook.com/', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.facebookUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.youtubeUrl', 'https://youtube.com/', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.youtubeUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.theme.preset', 'scheme-a', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.theme.preset');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.hero.eyebrow', 'SIMPLYCODES STYLE DEALS', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.hero.eyebrow');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.hero.title', 'Find verified promo codes that actually work', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.hero.title');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.hero.subtitle', 'Instant savings, clean checkout flow, and transparent code quality from real-time verification.', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.hero.subtitle');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.hero.bg.color', '#f7f9fd', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.hero.bg.color');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.hero.bg.imageUrl', '', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.hero.bg.imageUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.aboutUrl', '/about', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.aboutUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.privacyUrl', '/privacy', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.privacyUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.contactUrl', '/contact', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.contactUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.submitCouponUrl', '/submit-coupon', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.submitCouponUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'content.footer.affiliateDisclosureUrl', '/affiliate-disclosure', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'content.footer.affiliateDisclosureUrl');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.strip.enabled', 'true', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.strip.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.strip.text', 'Flash Sale: Save up to 70% today', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.strip.text');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.strip.link', 'https://example-affiliate.com/flash', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.strip.link');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.home.top.enabled', 'true', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.home.top.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.home.mid.enabled', 'true', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.home.mid.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.home.side.left.enabled', 'false', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.home.side.left.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.home.side.right.enabled', 'false', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.home.side.right.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.home.bottom.enabled', 'false', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.home.bottom.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.blog.top.enabled', 'false', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.blog.top.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.blog.inline.enabled', 'true', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.blog.inline.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.blog.bottom.enabled', 'false', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.blog.bottom.enabled');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.adsense.clientId', '', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.adsense.clientId');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.adsense.home.slot', '', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.adsense.home.slot');

INSERT INTO app_setting (setting_key, setting_value, updated_at)
SELECT 'ads.adsense.blog.slot', '', NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE setting_key = 'ads.adsense.blog.slot');

INSERT INTO crawler_site (site_key, site_name, base_url, active, coupon_enabled, brand_enabled, logo_enabled, created_at, updated_at)
SELECT 'retailmenot', 'RetailMeNot', 'https://www.retailmenot.com/', b'1', b'1', b'0', b'0', NOW(6), NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM crawler_site WHERE site_key = 'retailmenot');

INSERT INTO crawler_site (site_key, site_name, base_url, active, coupon_enabled, brand_enabled, logo_enabled, created_at, updated_at)
SELECT 'simplycodes', 'SimplyCodes', 'https://simplycodes.com/', b'1', b'1', b'0', b'0', NOW(6), NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM crawler_site WHERE site_key = 'simplycodes');
