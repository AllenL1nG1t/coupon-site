# Changelog

All notable changes to this project are documented in this file.
Workflow rule: every code change must update `CHANGELOG.md` and `CHANGELOG.zh-CN.md` together.

## [Unreleased]

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

### Docs
- Documented mandatory changelog update workflow in README files and changelog headers.
- Added startup workflow documentation: after successful startup, run `scripts/start-and-sync.ps1` to auto-commit/push latest code to GitHub.
- Added Linux one-click AWS deploy documentation and scripts:
  - `scripts/deploy-eb.sh`
  - `scripts/deploy-eb.env.example`

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

