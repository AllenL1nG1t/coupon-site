# æ›´æ–°æ—¥å¿—

æœ¬æ–‡ä»¶è®°å½•é¡¹ç›®æ¯ä¸ªç‰ˆæœ¬çš„é‡è¦å˜æ›´ã€‚
å·¥ä½œæµè§„åˆ™ï¼šæ¯æ¬¡ä»£ç æ”¹åŠ¨å¿…é¡»åŒæ­¥æ›´æ–° `CHANGELOG.md` ä¸Ž `CHANGELOG.zh-CN.md`ã€‚

## [Unreleased]

### ä¿®å¤
- ä¿®å¤åŽå°å¹¿å‘Šç®¡ç†åœ¨åˆ‡æ¢è¯­è¨€åŽ checkbox æ¶ˆå¤±çš„é—®é¢˜ï¼ˆä¿ç•™ label å†… input èŠ‚ç‚¹ï¼Œä»…æ›¿æ¢æ–‡æœ¬ï¼‰ã€‚
- æ–°å¢žåº•éƒ¨çŸ­é“¾æŽ¥è·¯ç”±è½¬å‘ï¼š
  - `/about` -> `/about.html`
  - `/privacy` -> `/privacy.html`
  - `/contact` -> `/contact.html`
  - `/submit-coupon` -> `/submit-coupon.html`
  - `/affiliate-disclosure` -> `/affiliate-disclosure.html`
- é¦–é¡µå¹¿å‘Šä½å·²æŽ¥å…¥åŽå°å¼€å…³æ¸²æŸ“ï¼ˆä¸å†åªæœ‰æ»šåŠ¨æ¡å¹¿å‘Šï¼‰ï¼š
  - `homeTopEnabled`ã€`homeMidEnabled`ã€`homeSideLeftEnabled`ã€`homeSideRightEnabled`ã€`homeBottomEnabled`
  - `blogTopEnabled`ã€`blogInlineEnabled`ã€`blogBottomEnabled`

### æ–‡æ¡£
- åœ¨ README ä¸Žæ›´æ–°æ—¥å¿—ä¸­å›ºåŒ–â€œæ¯æ¬¡æ”¹åŠ¨å¿…é¡»åŒæ­¥æ›´æ–°ä¸­è‹±æ–‡ changelogâ€çš„æµç¨‹ã€‚

- Added startup+sync workflow documentation: use scripts/start-and-sync.ps1 to auto commit/push after health check.

- Added Linux one-click deployment script and README usage: scripts/deploy-eb.sh / scripts/deploy-eb.env.example.

## [0.0.3] - 2026-02-21

### æ–°å¢ž
- åŽå°åˆ—è¡¨åˆ†é¡µä¸Žç­›é€‰æ¡†æž¶ï¼ˆCoupons/Brands/Blogs/Crawler Sites/Staged Couponsï¼‰ã€‚
- åŽå° Theme èœå•ï¼šä¸»é¢˜å±•ç¤ºåã€ç«™ç‚¹å/çŸ­è¯­ã€Logo æ–‡å­—/å›¾ç‰‡ã€‚
- åŽå° SEO èœå•ä¸Žç›¸å…³ APIã€‚
- çˆ¬è™«ç»Ÿè®¡æŠ¥è¡¨ API ä¸ŽåŽå°æŠ¥è¡¨è¡¨æ ¼ã€‚
- å“ç‰Œ Logo ä¸­é—´è¡¨ï¼ˆ`staged_brand_logo`ï¼‰åŠåŽå°å›¾ç‰‡é¢„è§ˆ APIã€‚
- é¡µè„šé™æ€é¡µé¢ï¼š
  - `about.html`
  - `privacy.html`
  - `contact.html`
  - `submit-coupon.html`
  - `affiliate-disclosure.html`

### è°ƒæ•´
- ç‰ˆæœ¬å‡çº§ä¸º `0.0.3`ï¼ˆåŽ»é™¤ SNAPSHOTï¼‰ã€‚
- ä¼˜æƒ åˆ¸æœ‰æ•ˆæœŸæ”¹ä¸ºæ—¥æœŸåŒ–è¡Œä¸ºï¼Œå‰ç«¯åŒºåˆ† active/expired å±•ç¤ºã€‚
- é¦–é¡µ deals å¡ç‰‡æ–°å¢žå“ç‰Œ Logo å±•ç¤ºã€‚
- åŽå°çˆ¬è™«çŠ¶æ€æ–‡æœ¬æ”¹ä¸ºå®Œæ•´å¯è¯»æè¿°ã€‚
- ä¸»é¢˜é…ç½®è¦†ç›–åˆ°æ›´å¤šå‰å°é¡µé¢ã€‚
- `db/init_mysql.sql` åŒæ­¥æ–°å¢žé…ç½®é¡¹ä¸Ž `staged_brand_logo` è¡¨ã€‚

### ä¿®å¤
- åŽå°å¹¿å‘Šç®¡ç† checkbox æ¶ˆå¤±é—®é¢˜ï¼ˆè¯­è¨€åˆ‡æ¢é€»è¾‘è¦†ç›–äº† `<label>` å†…éƒ¨èŠ‚ç‚¹ï¼‰ã€‚
- é¦–é¡µæ»šåŠ¨æ¡å¹¿å‘Šæ¢å¤ï¼ˆé€šè¿‡ `/api/ads/public` è¯»å–å¹¶æ¸²æŸ“ï¼‰ã€‚
- é¡µè„šé»˜è®¤é“¾æŽ¥æ”¹ä¸ºå¯è®¿é—®çš„ `.html` é¡µé¢ã€‚




