# Changelog

All notable changes to this project are documented in this file.
Workflow rule: every code change must update `CHANGELOG.md` and `CHANGELOG.zh-CN.md` together.

## [Unreleased]

### Added
- Added brand-level auto-post switch for staged coupons:
  - New `brand_profile.auto_post_coupons` field
  - Admin Brand form and table support this toggle
  - Newly staged coupons are auto-posted to main coupons when the brand toggle is enabled
- Added real-time refresh buttons for admin list sections:
  - Dashboard brand coupon stats
  - Coupons list
  - Brands list
  - Blogs list
  - Crawler Sites list
  - Staged Coupons list
  - Crawler logs/report/logo lists
- Added coupon-code page ad placement controls in admin Ads settings:
  - `codeTopEnabled`
  - `codeBottomEnabled`
  - `codeAdsenseSlot`
- Added crawler schedule list fields and persistence:
  - `crawler.*.run-at` (time-of-day trigger, `HH:mm`)
  - `crawler.*.last-run-at` (latest execution timestamp)
  - Admin schedule view now uses a table/list layout with per-task Run At / Last Run / Run Now.
- Added staged-coupon batch delete API and admin action:
  - `POST /api/admin/staged-coupons/delete-batch`
  - Admin "Delete Selected" button in crawler staging list.

### Changed
- Scheduled crawler defaults are now disabled for all modules by default:
  - Coupon crawler
  - Brand crawler
  - Brand logo crawler
  - Includes backend defaults, admin initial UI defaults, and `db/init_mysql.sql` seed defaults
- Scheduled crawler runtime now checks the daily run-time window before interval execution, and updates last-run timestamp after each crawler run.

### Fixed
- Restored admin Ads checkboxes when switching language by preserving checkbox nodes inside labels.
- Added route forwards for footer shortcut paths:
  - `/about` -> `/about.html`
  - `/privacy` -> `/privacy.html`
  - `/contact` -> `/contact.html`
  - `/submit-coupon` -> `/submit-coupon.html`
  - `/affiliate-disclosure` -> `/affiliate-disclosure.html`
- Implemented homepage ad slot rendering for all admin toggles:
  - `homeTopEnabled`, `homeMidEnabled`, `homeSideLeftEnabled`, `homeSideRightEnabled`, `homeBottomEnabled`
  - `blogTopEnabled`, `blogInlineEnabled`, `blogBottomEnabled`
- Improved coupon crawler run response text:
  - Replaced opaque `custom` summary with `customSites=[siteName=count,...]` based on Crawler Sites display names.
- Improved Brands table media visibility:
  - Added Logo preview and Hero preview thumbnail columns so images can be verified directly in admin list view.
- Normalized logo rendering sizes to prevent oversized source images from breaking layout.
- Coupon-code page now displays brand logo and supports top/bottom ad placement rendering from admin config.
- Fixed admin table search input focus loss while typing (controls now preserve focused field/caret after re-render).
- Expanded staged-coupon list filters:
  - Source filter (`RetailMeNot` / `SimplyCodes` / `Custom` / `Other`)
  - Main-link filter (`linked` / `unlinked`)
- Homepage hero "Verified Today / Working Codes" card now uses live coupon data instead of hardcoded text:
  - `Working Codes` = current non-expired coupon count
  - `updated today` = coupons with `updatedAt` (or `createdAt`) on current date
- Removed `custom` wording from crawler UI/runtime outputs for added crawler sites:
  - Staging source filter now shows concrete site names dynamically
  - Staging source column now displays site names instead of raw `custom-*` source keys
  - Coupon crawler run summary now uses `siteCrawler` and `sites=[siteName=count]`
  - Generic site crawler log source tags renamed to `site-*`

### Docs
- Documented mandatory changelog update workflow in README files and changelog headers.
- Added startup workflow documentation: after successful startup, run `scripts/start-and-sync.ps1` to auto-commit/push latest code to GitHub.
- Added Linux one-click AWS deploy documentation and scripts:
  - `scripts/deploy-eb.sh`
  - `scripts/deploy-eb.env.example`
- Added explicit workflow rule: every DB schema change must update `db/init_mysql.sql` and refresh its `Last Updated` timestamp.

## [0.0.3] - 2026-02-21

### Added
- Admin list pagination and filtering framework for Coupons, Brands, Blogs, Crawler Sites, and Staged Coupons.
- Admin Theme menu for site identity fields (theme display name, site name/slogan, logo text/image).
- Admin SEO menu and APIs.
- Crawler report API and dashboard table.
- Staged brand logo table, repository, service, and admin image preview API.
- Footer static pages:
  - `about.html`
  - `privacy.html`
  - `contact.html`
  - `submit-coupon.html`
  - `affiliate-disclosure.html`

### Changed
- Version updated from `0.0.2-SNAPSHOT`/`0.0.1-SNAPSHOT` lineage to `0.0.3`.
- Coupon expiry uses date-oriented behavior and frontend separates active vs expired rendering.
- Homepage deal cards now include brand logos.
- Crawler status text in admin uses full readable wording.
- Theme value is applied on more frontend pages.
- `db/init_mysql.sql` updated with new settings seeds and `staged_brand_logo` table.

### Fixed
- Admin Ads checkboxes disappearing issue caused by language rendering replacing `<label>` content.
- Ad strip rendering on homepage now restored by loading `/api/ads/public`.
- Footer default links now point to valid `.html` pages.

