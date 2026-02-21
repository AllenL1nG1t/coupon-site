# Dotiki Coupon

Language: [English (Default)](README.md) | [Chinese (Simplified)](README.zh-CN.md)

Dotiki Coupon site built with Java + Spring Boot + Maven.

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
- Upload:
  - `POST /api/admin/uploads/images` (multipart `file`)

## Known Limitation
- RetailMeNot can return `403` due to anti-bot protection. Fallback coupon seeding is used when crawling is blocked.

