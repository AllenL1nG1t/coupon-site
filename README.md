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

Default URL:
- Site: `http://localhost:8081/`
- Admin: `http://localhost:8081/admin.html`
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

## Core Features
- Search bar centered at top
- Trending stores with logo
- Coupon code hidden by default; reveal in modal on click
- Affiliate link auto-opens in new tab when coupon is revealed
- Coupon code and affiliate URL persisted in database
- Admin crawler switch and crawler logs

## API
- `GET /api/coupons?category=all&q=`
- `POST /api/coupons/{id}/reveal`
- `GET /api/admin/dashboard`
- `GET /api/admin/settings`
- `PUT /api/admin/settings`
- `GET /api/admin/logs`
- `POST /api/admin/crawler/run`

## Crawler
- Source attempt: RetailMeNot
- Scheduled run controlled by `crawler.enabled` setting in admin
- Manual run supported in admin

Current limitation:
- RetailMeNot often returns `403` due to anti-bot protection, so production usage needs proxy/browser automation or a different data source.

## Logos
Logos are now served from local static assets under:
- `src/main/resources/static/logos/`

The app normalizes existing DB records on startup so old remote logo URLs are replaced by local logo paths.