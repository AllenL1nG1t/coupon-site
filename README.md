# coupon-site

Coupon site built with Java + Spring Boot + Maven.

## Tech Stack
- Java 17
- Spring Boot 3.5
- Spring Web + Spring Data JPA
- H2 file database
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
- H2 Console: `http://localhost:8081/h2-console`

## Configuration
`src/main/resources/application.properties`:

```properties
server.port=8081
spring.datasource.url=jdbc:h2:file:./data/coupondb;AUTO_SERVER=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
crawler.fixed-delay-ms=1800000
admin.username=admin
admin.password=admin123
```

Notes:
- Database is persisted in `./data/coupondb*`
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
  - Enable/disable scheduled crawler
  - Manual run
  - Log viewing

## Coupon Flow
- Coupon code is hidden on list view.
- Clicking `Show Coupon Code` calls reveal API and **directly redirects current page** to configured affiliate URL.
- Coupon code and affiliate URL are persisted in DB.

## Crawlers
- Sources:
  - RetailMeNot
  - SimplyCodes
- Duplicate handling:
  - Deduped by `store + couponCode`
  - Same code for same store is skipped as duplicate
  - Different code is inserted as a new coupon
- Logs include inserted count and skipped duplicate count.

## Public API
- `GET /api/coupons?category=all&q=`
- `POST /api/coupons/{id}/reveal`
- `GET /api/blogs`
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
- RetailMeNot can return `403` due to anti-bot protection. Fallback coupon seeding is in place when crawling is blocked.
