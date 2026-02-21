# Dotiki Coupon
最后更新：2026-02-21 00:43:42 -05:00

Dotiki Coupon 是一个基于 Java + Spring Boot + Maven 的优惠券网站项目。

## 技术栈
- Java 17
- Spring Boot 3.5
- Spring Web + Spring Data JPA
- MySQL
- 前端：静态 HTML/CSS/JavaScript

## 启动方式
```bash
mvn spring-boot:run
```

默认访问地址：
- 站点首页：`http://localhost:8081/`
- 分类页：`http://localhost:8081/categories.html`
- 商家页：`http://localhost:8081/stores.html`
- 返现页：`http://localhost:8081/cashback.html`
- 后台登录：`http://localhost:8081/admin-login.html`
- 后台面板：`http://localhost:8081/admin-panel.html`

## 配置
配置文件：`src/main/resources/application.properties`

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

说明：
- 可直接在 `application.properties` 写死数据库连接，也可用环境变量覆盖（`DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USERNAME`、`DB_PASSWORD`）。
- 当系统中没有管理员账号时，会按 `admin.default.username` / `admin.default.password` 自动初始化到 `admin_user` 表。
- MySQL 初始化脚本在 `db/init_mysql.sql`。
- 如果改了数据库结构，请同步更新 `db/init_mysql.sql`。
- 上传图片存储目录：`./data/uploads`

## 后台功能
- Coupon 增删改查（含 affiliate URL、coupon code）
- Brand Profile 增删改查（用于商家详情页）
- Blog 增删改查（支持图片上传）
- 广告位配置：
  - 顶部滚动条广告
  - 首页广告位（`top` / `mid` / `bottom`）
  - 博客页广告位（`top` / `inline` / `bottom`）
  - Google AdSense Client/Slot
- 首页内容配置：
  - Eyebrow / Title / Subtitle
  - 背景色
  - 背景图（支持上传）
- 爬虫配置：
  - 独立开关：Coupon / Brand / Brand Logo
  - 独立定时（分钟）
  - 分模块手动执行
  - 日志查看
  - 品牌 Logo 写入数据库字段 `brand_profile.logo_image`

## 优惠券展示流程
- 列表页默认隐藏 coupon code。
- 点击 `Show Coupon Code` 后调用 reveal API，并跳转到配置的 affiliate URL。
- 若优惠券级别 affiliate URL 为空，则回退到品牌级 affiliate URL。

## 爬虫说明
- 数据源：
  - RetailMeNot
  - SimplyCodes
  - 自定义站点（后台可配置）
- 去重规则：
  - 按 `store + couponCode` 去重
  - 同店同码跳过
  - 同店不同码新增

## 对外 API
- `GET /api/coupons?category=all&q=`
- `POST /api/coupons/{id}/reveal`
- `GET /api/blogs`
- `GET /api/brands`
- `GET /api/brands/detail?slug=`
- `GET /api/brands/by-store?store=`
- `GET /api/brands/logo?slug=`
- `GET /api/ads/public`
- `GET /api/content/public`

## 管理 API
- 鉴权：
  - `POST /api/admin/auth/login`
  - `POST /api/admin/auth/logout`
  - `GET /api/admin/auth/status`
- 设置和日志：
  - `GET /api/admin/settings`
  - `PUT /api/admin/settings`
  - `GET /api/admin/logs`
  - `POST /api/admin/crawler/run`
  - `POST /api/admin/crawler/run-coupons`
  - `POST /api/admin/crawler/run-brands`
  - `POST /api/admin/crawler/run-brand-logos`
- 优惠券：
  - `GET /api/admin/coupons`
  - `PUT /api/admin/coupons`
  - `DELETE /api/admin/coupons?id={id}`
- 品牌：
  - `GET /api/admin/brands`
  - `PUT /api/admin/brands`
  - `DELETE /api/admin/brands?id={id}`
- 博客：
  - `GET /api/admin/blogs`
  - `PUT /api/admin/blogs`
  - `DELETE /api/admin/blogs?id={id}`
- 广告：
  - `GET /api/admin/ads`
  - `PUT /api/admin/ads`
- 内容：
  - `GET /api/admin/content`
  - `PUT /api/admin/content`
- 上传：
  - `POST /api/admin/uploads/images`（multipart 字段 `file`）

## 已知限制
- RetailMeNot 可能因为反爬返回 `403`，触发 fallback 数据写入逻辑。
