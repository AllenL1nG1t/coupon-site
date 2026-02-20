# coupon-site

Coupon site based on Java + Spring Boot + Maven.

## Stack
- Java 17
- Spring Boot 3.5
- Spring Web + Spring Data JPA
- H2 file database
- Frontend: static HTML/CSS/JS

## Run
```bash
mvn spring-boot:run
```

Default URLs:
- Site: `http://localhost:8081/`
- Admin Login: `http://localhost:8081/admin-login.html`
- Admin Panel: `http://localhost:8081/admin-panel.html`
- H2 Console: `http://localhost:8081/h2-console`

## Database
Configured in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:file:./data/coupondb;AUTO_SERVER=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

Notes:
- Database file is created in `./data/`
- Tables are auto-created/updated by JPA (`spring.jpa.hibernate.ddl-auto=update`)

## Admin Login
Default credentials (change in `application.properties`):

```properties
admin.username=admin
admin.password=admin123
```

## Core Features
- Search bar centered at top
- Trending stores with logo
- Coupon code hidden by default; reveal in modal on click
- Affiliate URL is triggered in background (user stays on current page)
- Coupon and affiliate URL are persisted in DB
- Full admin management for coupons and affiliate links
- Admin crawler switch + manual run + crawler logs
- Blog section on homepage
- Blog content managed in admin panel
- Blog image upload supported from admin panel

## Public API
- `GET /api/coupons?category=all&q=`
- `POST /api/coupons/{id}/reveal`
- `GET /api/blogs`

## Admin API
- Auth
  - `POST /api/admin/auth/login`
  - `POST /api/admin/auth/logout`
  - `GET /api/admin/auth/status`
- Crawler
  - `GET /api/admin/settings`
  - `PUT /api/admin/settings`
  - `GET /api/admin/logs`
  - `POST /api/admin/crawler/run`
- Coupons
  - `GET /api/admin/coupons`
  - `PUT /api/admin/coupons`
  - `DELETE /api/admin/coupons?id={id}`
- Blogs
  - `GET /api/admin/blogs`
  - `PUT /api/admin/blogs`
  - `DELETE /api/admin/blogs?id={id}`
- Upload
  - `POST /api/admin/uploads/images` (multipart `file`)

## Crawler Note
RetailMeNot frequently returns `403` due to anti-bot protections. The crawler flow and admin controls are implemented, but stable production scraping needs a stronger strategy.

## Logos
Logos are served from local static assets:
- `src/main/resources/static/logos/`

Existing DB records are normalized on startup so remote logo URLs are replaced with local paths.