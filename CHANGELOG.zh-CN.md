# 更新日志

本文件记录项目每个版本的重要变更。
工作流规则：每次代码改动必须同步更新 `CHANGELOG.md` 与 `CHANGELOG.zh-CN.md`。

## [Unreleased]

### 修复
- 修复后台广告管理在切换语言后 checkbox 消失的问题（保留 label 内 input 节点，仅替换文本）。
- 新增页脚快捷路径路由转发：
  - `/about` -> `/about.html`
  - `/privacy` -> `/privacy.html`
  - `/contact` -> `/contact.html`
  - `/submit-coupon` -> `/submit-coupon.html`
  - `/affiliate-disclosure` -> `/affiliate-disclosure.html`
- 首页广告位已接入后台开关渲染（不再只有滚动条广告）：
  - `homeTopEnabled`、`homeMidEnabled`、`homeSideLeftEnabled`、`homeSideRightEnabled`、`homeBottomEnabled`
  - `blogTopEnabled`、`blogInlineEnabled`、`blogBottomEnabled`

### 文档
- 在 README 与更新日志中固化“每次改动必须同步更新中英文 changelog”的流程。
- 新增“启动成功后自动同步 GitHub”工作流说明：使用 `scripts/start-and-sync.ps1` 完成健康检查后自动 commit/push。
- 新增 Linux 一键部署脚本与 README 使用说明：
  - `scripts/deploy-eb.sh`
  - `scripts/deploy-eb.env.example`

## [0.0.3] - 2026-02-21

### 新增
- 后台列表分页与筛选框架（Coupons、Brands、Blogs、Crawler Sites、Staged Coupons）。
- 后台 Theme 菜单：主题展示名、站点名/短语、Logo 文字/图片。
- 后台 SEO 菜单与相关 API。
- 爬虫统计报表 API 与后台报表表格。
- 品牌 Logo 中间表（`staged_brand_logo`）及后台图片预览 API。
- 页脚静态页面：
  - `about.html`
  - `privacy.html`
  - `contact.html`
  - `submit-coupon.html`
  - `affiliate-disclosure.html`

### 调整
- 版本升级为 `0.0.3`（移除 SNAPSHOT）。
- 优惠券有效期改为日期化行为，前端区分 active/expired 展示。
- 首页 deals 卡片新增品牌 Logo 展示。
- 后台爬虫状态文本改为完整可读描述。
- 主题配置覆盖到更多前台页面。
- `db/init_mysql.sql` 同步新增配置项与 `staged_brand_logo` 表。

### 修复
- 后台广告管理 checkbox 消失问题（语言切换逻辑覆盖了 `<label>` 内部节点）。
- 首页滚动条广告恢复（通过 `/api/ads/public` 读取并渲染）。
- 页脚默认链接改为可访问的 `.html` 页面。
