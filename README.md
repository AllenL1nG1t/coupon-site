# Dotiki Coupon

- Language: [English (Default)](README.md) | [Chinese (Simplified)](README.zh-CN.md)
- Version: 0.0.3
- Last Updated: 2026-02-21 16:55:00 -05:00

Dotiki Coupon site built with Java + Spring Boot + Maven.

## Versioning
- Current version is stored in `VERSION`.
- Every update should bump/update the version and sync it in both `README.md` and `README.zh-CN.md`.
- Changelog:
  - English: `CHANGELOG.md`
  - Chinese: `CHANGELOG.zh-CN.md`
- Workflow rule:
  - Every feature/fix/refactor must append a clear entry to both changelog files in the same commit.
  - Every successful local startup must use `scripts/start-and-sync.ps1` so latest code is auto-synced to GitHub.

## Recent Updates
- Unified coupon click redirect flow across home, brand, and catalog pages:
  - Current tab navigates to affiliate URL
  - New tab opens `coupon-code.html`
- Added cache-busting for homepage script loading to avoid stale `app.js` behavior.
- Added admin list pagination/filter support (50/100/200 per page, date range, keyword/status filters).
- Added Theme menu and SEO menu in admin panel.
- Added crawler report API + staged brand logo table and image preview endpoint.
- Added footer pages (`about/privacy/contact/submit-coupon/affiliate-disclosure`).
- Coupon expiry now uses date format (`YYYY-MM-DD`) and expired coupons are separated in frontend views.

## Tech Stack
- Java 17
- Spring Boot 3.5
- Spring Web + Spring Data JPA
- MySQL
- Frontend: static HTML/CSS/JavaScript

## Run
```bash
mvn spring-boot:run
```

Recommended workflow (startup + auto push to GitHub after health check):
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\start-and-sync.ps1
```

## Deploy To AWS (One Command)
1. Install prerequisites on Linux: `awscli`, `eb`, `mvn`, `git`.
2. Copy env template and fill real values:
```bash
cp scripts/deploy-eb.env.example scripts/deploy-eb.env
```
3. Run one-click deploy:
```bash
chmod +x scripts/deploy-eb.sh
./scripts/deploy-eb.sh -f scripts/deploy-eb.env
```

What this script does:
- Builds the project jar (`mvn clean package`).
- Initializes EB app (`eb init`) if needed.
- Creates/uses target EB environment.
- Sets DB env vars (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`).
- Deploys staged code to EB.

Default URLs:
- Site: `http://localhost:8081/`
- Categories page: `http://localhost:8081/categories.html`
- Stores page: `http://localhost:8081/stores.html`
- Cash Back page: `http://localhost:8081/cashback.html`
- Admin Login: `http://localhost:8081/admin-login.html`
- Admin Panel: `http://localhost:8081/admin-panel.html`

## Configuration
`src/main/resources/application.properties`:

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:coupon_site}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
spring.jpa.hibernate.ddl-auto=update
crawler.fixed-delay-ms=1800000
admin.default.username=admin
admin.default.password=admin123
```

Notes:
- Configure DB host/port/name/user/password directly in `application.properties`, or override with env vars (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`)
- Default admin account is seeded from `admin.default.username` and `admin.default.password` into DB table `admin_user` when no admin exists.
- MySQL initialization script is provided at `db/init_mysql.sql`.
- When database schema changes, update `db/init_mysql.sql` in the same commit.
- Uploaded images are stored under `./data/uploads`

## Admin Features
- Coupon CRUD (including affiliate URL and coupon code)
- Brand profile CRUD (used by brand detail pages)
- Blog CRUD with image upload
- Ads management:
  - Scrolling strip ad
  - Home placements (`top`, `mid`, `bottom`)
  - Blog placements (`top`, `inline`, `bottom`)
  - Google AdSense client/slot settings
- Homepage Hero content management:
  - Eyebrow/title/subtitle
  - Background color
  - Background image (upload supported)
- Theme & site identity management:
  - Theme preset + custom theme display name
  - Site name/slogan
  - Site logo text/image upload
- SEO management:
  - Meta title/description/keywords
  - OG image URL
  - Canonical base URL
- Crawler management:
  - Separate scheduled switches:
    - Coupon crawler
    - Brand profile crawler
    - Brand logo crawler
  - Separate interval (minutes) for each crawler from admin panel
  - Manual run buttons for each crawler
  - Log viewing
  - Brand logos are crawled and stored in DB (`brand_profile.logo_image`)

## Coupon Flow
- Coupon code is hidden on list view.
- Clicking `Show Coupon Code` calls reveal API and redirects user to configured affiliate URL.
- If coupon-level affiliate URL is empty, reveal flow falls back to store-level brand affiliate URL.

## Crawlers
- Sources:
  - RetailMeNot
  - SimplyCodes
  - Custom configured sites
- Duplicate handling:
  - Deduped by `store + couponCode`
  - Same code for same store is skipped as duplicate
  - Different code is inserted as a new coupon

## Public API
- `GET /api/coupons?category=all&q=`
- `POST /api/coupons/{id}/reveal`
- `GET /api/blogs`
- `GET /api/brands`
- `GET /api/brands/detail?slug=`
- `GET /api/brands/by-store?store=`
- `GET /api/brands/logo?slug=`
- `GET /api/ads/public`
- `GET /api/content/public`
- `GET /api/seo/public`

## Admin API
- Auth:
  - `POST /api/admin/auth/login`
  - `POST /api/admin/auth/logout`
  - `GET /api/admin/auth/status`
- Settings and logs:
  - `GET /api/admin/settings`
  - `PUT /api/admin/settings`
  - `GET /api/admin/logs`
  - `POST /api/admin/crawler/run`
  - `POST /api/admin/crawler/run-coupons`
  - `POST /api/admin/crawler/run-brands`
  - `POST /api/admin/crawler/run-brand-logos`
  - `GET /api/admin/crawler/report`
  - `GET /api/admin/staged-brand-logos`
  - `GET /api/admin/staged-brand-logos/image?id=`
- Coupons:
  - `GET /api/admin/coupons`
  - `PUT /api/admin/coupons`
  - `DELETE /api/admin/coupons?id={id}`
- Brands:
  - `GET /api/admin/brands`
  - `PUT /api/admin/brands`
  - `DELETE /api/admin/brands?id={id}`
- Blogs:
  - `GET /api/admin/blogs`
  - `PUT /api/admin/blogs`
  - `DELETE /api/admin/blogs?id={id}`
- Ads:
  - `GET /api/admin/ads`
  - `PUT /api/admin/ads`
- Content:
  - `GET /api/admin/content`
  - `PUT /api/admin/content`
- SEO:
  - `GET /api/admin/seo`
  - `PUT /api/admin/seo`
- Upload:
  - `POST /api/admin/uploads/images` (multipart `file`)

## Known Limitation
- RetailMeNot can return `403` due to anti-bot protection. Fallback coupon seeding is used when crawling is blocked.


