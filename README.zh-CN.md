# Dotiki Coupon

- 语言: [English (Default)](README.md) | [Chinese (Simplified)](README.zh-CN.md)
- 项目版本: 0.0.3
- 最后更新: 2026-02-21 16:55:00 -05:00

Dotiki Coupon 是一个基于 Java + Spring Boot + Maven 的优惠券网站项目。

## 版本管理
- 当前版本保存在 `VERSION`。
- 每次更新都要同步修改版本号，并同步到 `README.md` 和 `README.zh-CN.md`。
- Changelog:
  - English: `CHANGELOG.md`
  - Chinese: `CHANGELOG.zh-CN.md`
- 工作流规则:
  - 每个 feature/fix/refactor 必须在同一次提交中同步更新中英文 changelog。
  - 每次本地启动成功后，必须使用 `scripts/start-and-sync.ps1` 自动同步最新代码到 GitHub。

## 最近更新
- 统一首页、品牌页、分类/商家页的优惠券跳转逻辑:
  - 当前页跳转到联盟链接
  - 新标签页打开 `coupon-code.html`
- 首页脚本增加缓存版本参数，避免浏览器继续加载旧 `app.js`。
- 后台列表支持分页/筛选（每页 50/100/200，日期范围、关键词、状态等）。
- 后台新增 Theme 菜单和 SEO 菜单。
- 新增爬虫报表 API + 品牌 logo 中间表及预览接口。
- 新增页脚页面（about/privacy/contact/submit-coupon/affiliate-disclosure）。
- 优惠券有效期改为日期格式（`YYYY-MM-DD`），前端区分 active/expired 展示。

## 技术栈
- Java 17
- Spring Boot 3.5
- Spring Web + Spring Data JPA
- MySQL
- 前端: 静态 HTML/CSS/JavaScript

## 启动方式
```bash
mvn spring-boot:run
```

推荐工作流（启动成功后自动推送 GitHub）:
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\start-and-sync.ps1
```

## AWS 一键部署（Linux）
1. 安装依赖: `awscli`、`eb`、`mvn`、`git`。
2. 复制环境变量模板并填写真实值:
```bash
cp scripts/deploy-eb.env.example scripts/deploy-eb.env
```
3. 执行一键部署:
```bash
chmod +x scripts/deploy-eb.sh
./scripts/deploy-eb.sh -f scripts/deploy-eb.env
```

脚本会自动完成:
- `mvn clean package` 打包
- `eb init` 初始化应用（如未初始化）
- 创建或切换到目标 EB 环境
- 注入数据库环境变量（`DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USERNAME`、`DB_PASSWORD`）
- 触发 `eb deploy --staged` 部署

默认访问地址:
- 站点首页: `http://localhost:8081/`
- 分类页: `http://localhost:8081/categories.html`
- 商家页: `http://localhost:8081/stores.html`
- 返现页: `http://localhost:8081/cashback.html`
- 后台登录: `http://localhost:8081/admin-login.html`
- 后台面板: `http://localhost:8081/admin-panel.html`

## 配置
配置文件: `src/main/resources/application.properties`

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

说明:
- 可直接在 `application.properties` 配置数据库连接，也可用环境变量覆盖（`DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USERNAME`、`DB_PASSWORD`）。
- 系统没有管理员账号时，会按 `admin.default.username` / `admin.default.password` 初始化到 `admin_user` 表。
- MySQL 初始化脚本位于 `db/init_mysql.sql`。
- 数据库结构变更时，请同步更新 `db/init_mysql.sql`。
- 上传图片目录: `./data/uploads`

## 已知限制
- RetailMeNot 可能因反爬返回 `403`，会触发 fallback 数据写入逻辑。

