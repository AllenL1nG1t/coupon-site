# 更新日志

本文件记录项目每个版本的重要变更。
工作流规则：每次代码改动必须同步更新 `CHANGELOG.md` 与 `CHANGELOG.zh-CN.md`。

## [Unreleased]

### 新增
- 新增品牌级“中间表自动推送主站”开关：
  - `brand_profile.auto_post_coupons` 字段
  - 后台品牌表单与品牌列表支持该开关
  - 当品牌开启该开关时，爬虫写入中间表后会自动发布到主站优惠券表
- 后台各列表区域新增“实时刷新”按钮：
  - 仪表盘品牌优惠券统计
  - 优惠券列表
  - 品牌列表
  - 博客列表
  - 爬虫站点列表
  - 爬虫中间表列表
  - 爬虫日志/报表/Logo 列表
- 后台 Ads 新增优惠码页广告位配置：
  - `codeTopEnabled`
  - `codeBottomEnabled`
  - `codeAdsenseSlot`
- 新增爬虫定时列表能力与配置项：
  - `crawler.*.run-at`（按天执行时间，`HH:mm`）
  - `crawler.*.last-run-at`（最后一次执行时间）
  - 后台“定时任务”改为列表/表格展示，包含每项任务的执行时间、最后运行时间与单独立即执行按钮。
- 新增中间表批量删除能力：
  - 后端接口 `POST /api/admin/staged-coupons/delete-batch`
  - 后台中间表列表新增“删除选中项”按钮。

### 调整
- 三类定时爬虫默认改为关闭（需后台手动开启）：
  - 优惠券爬虫
  - 品牌爬虫
  - 品牌 Logo 爬虫
  - 已同步后端默认值、后台初始展示默认值、`db/init_mysql.sql` 种子默认值
- 定时爬虫执行逻辑新增“每日运行时间窗口”判断，并在每次执行后写回对应的最后运行时间。

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
- 优化优惠券爬虫执行结果文案：
  - 将不易理解的 `custom` 汇总改为 `customSites=[站点名=数量,...]`，站点名取自“站点管理”里的 `siteName`。
- 优化品牌管理的图片可视化：
  - 品牌列表新增 Logo 预览和主图预览缩略图列，不再只能看 URL。
- 统一 Logo 渲染尺寸，避免源图过大导致页面排版错乱。
- 优惠码展示页新增品牌 Logo 展示，并支持按后台配置渲染顶部/底部广告位。
- 修复后台列表搜索框输入时失焦的问题（重渲染后保留焦点与光标）。
- 爬虫中间表列表新增更多过滤条件：
  - 来源筛选（RetailMeNot / SimplyCodes / Custom / Other）
  - 主站关联筛选（已关联 / 未关联）
- 首页 Hero 区 `Verified Today / Working Codes` 卡片改为真实数据展示，不再写死：
  - `Working Codes` = 当前未过期优惠券数量
  - `updated today` = 当天 `updatedAt`（无则 `createdAt`）的优惠券数量
- 去掉新增爬虫站点相关展示中的 `custom` 字样：
  - 中间表来源筛选改为动态站点名
  - 中间表来源列改为显示站点名，不再显示 `custom-*`
  - 优惠券爬虫执行结果文案改为 `siteCrawler` 与 `sites=[站点名=数量]`
  - 通用站点爬虫日志 source tag 统一改为 `site-*`

### 文档
- 在 README 与更新日志中固化“每次改动必须同步更新中英文 changelog”的流程。
- 新增“启动成功后自动同步 GitHub”工作流说明：使用 `scripts/start-and-sync.ps1` 完成健康检查后自动 commit/push。
- 新增 Linux 一键部署脚本与 README 使用说明：
  - `scripts/deploy-eb.sh`
  - `scripts/deploy-eb.env.example`
- 明确新增工作流规则：数据库结构变更时必须同步更新 `db/init_mysql.sql` 并刷新其 `Last Updated` 时间戳。

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
