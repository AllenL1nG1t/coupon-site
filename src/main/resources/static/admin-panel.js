const couponCrawlerEnabled = document.getElementById("couponCrawlerEnabled");
const brandCrawlerEnabled = document.getElementById("brandCrawlerEnabled");
const brandLogoCrawlerEnabled = document.getElementById("brandLogoCrawlerEnabled");
const couponCrawlerIntervalMinutes = document.getElementById("couponCrawlerIntervalMinutes");
const brandCrawlerIntervalMinutes = document.getElementById("brandCrawlerIntervalMinutes");
const brandLogoCrawlerIntervalMinutes = document.getElementById("brandLogoCrawlerIntervalMinutes");
const saveCrawlerBtn = document.getElementById("saveCrawlerBtn");
const runCouponCrawlerBtn = document.getElementById("runCouponCrawlerBtn");
const runBrandCrawlerBtn = document.getElementById("runBrandCrawlerBtn");
const runBrandLogoCrawlerBtn = document.getElementById("runBrandLogoCrawlerBtn");
const crawlerStatus = document.getElementById("crawlerStatus");
const crawlerSiteName = document.getElementById("crawlerSiteName");
const crawlerSiteBaseUrl = document.getElementById("crawlerSiteBaseUrl");
const crawlerSiteActive = document.getElementById("crawlerSiteActive");
const crawlerSiteCouponEnabled = document.getElementById("crawlerSiteCouponEnabled");
const crawlerSiteBrandEnabled = document.getElementById("crawlerSiteBrandEnabled");
const crawlerSiteLogoEnabled = document.getElementById("crawlerSiteLogoEnabled");
const saveCrawlerSiteBtn = document.getElementById("saveCrawlerSiteBtn");
const crawlerSiteTable = document.getElementById("crawlerSiteTable");
const stagedCouponTable = document.getElementById("stagedCouponTable");
const postSelectedStagedBtn = document.getElementById("postSelectedStagedBtn");
const logPanel = document.getElementById("logPanel");
const logLang = document.getElementById("logLang");
const crawlerReportTable = document.getElementById("crawlerReportTable");
const stagedBrandLogoTable = document.getElementById("stagedBrandLogoTable");
const adminLang = document.getElementById("adminLang");
const quickThemePreset = document.getElementById("quickThemePreset");
const couponTable = document.getElementById("couponTable");
const blogTable = document.getElementById("blogTable");
const brandTable = document.getElementById("brandTable");
const logoutBtn = document.getElementById("logoutBtn");
const tabsRoot = document.getElementById("adminTabs");
const crawlerSubTabs = document.getElementById("crawlerSubTabs");

const statCoupons = document.getElementById("statCoupons");
const statBlogs = document.getElementById("statBlogs");
const statBrands = document.getElementById("statBrands");
const statCrawler = document.getElementById("statCrawler");
const brandCouponStatTable = document.getElementById("brandCouponStatTable");

const contentHeroEyebrow = document.getElementById("contentHeroEyebrow");
const contentHeroTitle = document.getElementById("contentHeroTitle");
const contentHeroSubtitle = document.getElementById("contentHeroSubtitle");
const contentHeroBgColor = document.getElementById("contentHeroBgColor");
const contentHeroBgColorPicker = document.getElementById("contentHeroBgColorPicker");
const contentHeroBgImageUrl = document.getElementById("contentHeroBgImageUrl");
const contentThemePreset = document.getElementById("contentThemePreset");
const themeDisplayName = document.getElementById("themeDisplayName");
const siteName = document.getElementById("siteName");
const siteSlogan = document.getElementById("siteSlogan");
const siteLogoText = document.getElementById("siteLogoText");
const siteLogoImageUrl = document.getElementById("siteLogoImageUrl");
const siteLogoImageFile = document.getElementById("siteLogoImageFile");
const uploadSiteLogoBtn = document.getElementById("uploadSiteLogoBtn");
const saveThemeBtn = document.getElementById("saveThemeBtn");
const themeStatus = document.getElementById("themeStatus");
const contentFooterTagline = document.getElementById("contentFooterTagline");
const contentFooterAboutUrl = document.getElementById("contentFooterAboutUrl");
const contentFooterPrivacyUrl = document.getElementById("contentFooterPrivacyUrl");
const contentFooterContactUrl = document.getElementById("contentFooterContactUrl");
const contentFooterSubmitCouponUrl = document.getElementById("contentFooterSubmitCouponUrl");
const contentFooterAffiliateDisclosureUrl = document.getElementById("contentFooterAffiliateDisclosureUrl");
const contentFooterTwitterUrl = document.getElementById("contentFooterTwitterUrl");
const contentFooterInstagramUrl = document.getElementById("contentFooterInstagramUrl");
const contentFooterFacebookUrl = document.getElementById("contentFooterFacebookUrl");
const contentFooterYoutubeUrl = document.getElementById("contentFooterYoutubeUrl");
const heroImageFile = document.getElementById("heroImageFile");
const uploadHeroImageBtn = document.getElementById("uploadHeroImageBtn");
const saveContentBtn = document.getElementById("saveContentBtn");
const contentStatus = document.getElementById("contentStatus");

const couponStore = document.getElementById("couponStore");
const couponTitle = document.getElementById("couponTitle");
const couponCategory = document.getElementById("couponCategory");
const couponExpires = document.getElementById("couponExpires");
const couponCode = document.getElementById("couponCode");
const couponAffiliate = document.getElementById("couponAffiliate");
const couponLogo = document.getElementById("couponLogo");
const saveCouponBtn = document.getElementById("saveCouponBtn");
const clearCouponBtn = document.getElementById("clearCouponBtn");
const saveAllCouponsBtn = document.getElementById("saveAllCouponsBtn");

const brandStoreName = document.getElementById("brandStoreName");
const brandSlug = document.getElementById("brandSlug");
const brandTitle = document.getElementById("brandTitle");
const brandSummary = document.getElementById("brandSummary");
const brandOfficialUrl = document.getElementById("brandOfficialUrl");
const brandAffiliateUrl = document.getElementById("brandAffiliateUrl");
const brandLogoUrl = document.getElementById("brandLogoUrl");
const brandHeroImageUrl = document.getElementById("brandHeroImageUrl");
const brandDescription = document.getElementById("brandDescription");
const saveBrandBtn = document.getElementById("saveBrandBtn");
const clearBrandBtn = document.getElementById("clearBrandBtn");
const saveAllBrandsBtn = document.getElementById("saveAllBrandsBtn");

const blogTitle = document.getElementById("blogTitle");
const blogSummary = document.getElementById("blogSummary");
const blogCover = document.getElementById("blogCover");
const blogPublished = document.getElementById("blogPublished");
const blogContent = document.getElementById("blogContent");
const saveBlogBtn = document.getElementById("saveBlogBtn");
const clearBlogBtn = document.getElementById("clearBlogBtn");
const saveAllBlogsBtn = document.getElementById("saveAllBlogsBtn");
const blogImageFile = document.getElementById("blogImageFile");
const uploadImageBtn = document.getElementById("uploadImageBtn");
const blogStatus = document.getElementById("blogStatus");
const blogContentEditor = document.getElementById("blogContentEditor");
const blogEditorToolbar = document.getElementById("blogEditorToolbar");

const seoTitle = document.getElementById("seoTitle");
const seoDescription = document.getElementById("seoDescription");
const seoKeywords = document.getElementById("seoKeywords");
const seoOgImageUrl = document.getElementById("seoOgImageUrl");
const seoCanonicalBase = document.getElementById("seoCanonicalBase");
const saveSeoBtn = document.getElementById("saveSeoBtn");
const seoStatus = document.getElementById("seoStatus");

const adsStripEnabled = document.getElementById("adsStripEnabled");
const adsStripText = document.getElementById("adsStripText");
const adsStripLink = document.getElementById("adsStripLink");
const adsHomeTopEnabled = document.getElementById("adsHomeTopEnabled");
const adsHomeMidEnabled = document.getElementById("adsHomeMidEnabled");
const adsHomeSideLeftEnabled = document.getElementById("adsHomeSideLeftEnabled");
const adsHomeSideRightEnabled = document.getElementById("adsHomeSideRightEnabled");
const adsHomeBottomEnabled = document.getElementById("adsHomeBottomEnabled");
const adsBlogTopEnabled = document.getElementById("adsBlogTopEnabled");
const adsBlogInlineEnabled = document.getElementById("adsBlogInlineEnabled");
const adsBlogBottomEnabled = document.getElementById("adsBlogBottomEnabled");
const adsenseClientId = document.getElementById("adsenseClientId");
const adsenseHomeSlot = document.getElementById("adsenseHomeSlot");
const adsenseBlogSlot = document.getElementById("adsenseBlogSlot");
const saveAdsBtn = document.getElementById("saveAdsBtn");
const adsStatus = document.getElementById("adsStatus");

let cachedCoupons = [];
let cachedBlogs = [];
let cachedBrands = [];
let cachedCrawlerSites = [];
let cachedCrawlerLogs = [];
let cachedStagedCoupons = [];
let selectedStagedCouponIds = new Set();
let currentLang = localStorage.getItem("admin.lang") || "en";

const I18N = {
  en: {
    theme: "Theme",
    language: "Language",
    navSite: "Site",
    tabDashboard: "Dashboard",
    tabContent: "Content",
    tabTheme: "Theme",
    tabSeo: "SEO",
    tabCoupons: "Coupons",
    tabBrands: "Brands",
    tabBlogs: "Blogs",
    tabAds: "Ads",
    tabCrawler: "Crawler",
    crawlerSubtabSchedule: "Schedule",
    crawlerSubtabSites: "Sites",
    crawlerSubtabStaging: "Staging",
    crawlerSubtabLogs: "Logs",
    dashboardTitle: "Dashboard",
    statCouponsLabel: "Total Coupons",
    statBlogsLabel: "Published Blogs",
    statBrandsLabel: "Total Brands",
    statCrawlerLabel: "Crawler Status",
    contentSectionTitle: "Homepage Hero & Footer Content",
    contentSectionDesc: "Edit hero text, background image and footer social links",
    contentThemeLabel: "Theme Preset",
    couponSectionDesc: "Manage coupon info and affiliate URL",
    brandSectionDesc: "Used by Trending Stores detail pages",
    blogSectionDesc: "Create/edit homepage blog content",
    adsSectionDesc: "Configure strip ad, homepage/blog placements, and AdSense IDs",
    adsCardStripTitle: "Scrolling Strip",
    adsCardHomeTitle: "Homepage Placements",
    adsCardBlogTitle: "Blog Placements",
    adsCardSenseTitle: "Google AdSense",
    crawlerSectionDesc: "Coupon / Brand / Logo crawler split controls",
    crawlerSitesDesc: "Add websites and control coupon/brand/logo crawling per site",
    crawlerBuiltinHint: "Built-in: RetailMeNot / SimplyCodes can be toggled below too",
    crawlerLogsTitle: "Crawler Logs",
    stagedCouponsTitle: "Crawler Staging Queue",
    stagedCouponsDesc: "Crawler data is stored here first. Select and post to main site manually.",
    stagedCouponsHint: "Rows already posted are marked.",
    postSelectedStagedBtn: "Post Selected to Main Site",
    logLanguage: "Log Language",
    adsStripEnabledLabel: "Enable scrolling strip ad",
    adsHomeTopEnabledLabel: "Home top ad",
    adsHomeMidEnabledLabel: "Home mid ad",
    adsHomeSideLeftEnabledLabel: "Home middle left ad",
    adsHomeSideRightEnabledLabel: "Home middle right ad",
    adsHomeBottomEnabledLabel: "Home bottom ad",
    adsBlogTopEnabledLabel: "Blog top ad",
    adsBlogInlineEnabledLabel: "Blog inline ad",
    adsBlogBottomEnabledLabel: "Blog bottom ad",
    couponCrawlerEnabledLabel: "Enable scheduled coupon crawler",
    couponCrawlerIntervalLabel: "Coupon interval (minutes)",
    brandCrawlerEnabledLabel: "Enable scheduled brand crawler",
    brandCrawlerIntervalLabel: "Brand interval (minutes)",
    brandLogoCrawlerEnabledLabel: "Enable scheduled brand logo crawler",
    brandLogoCrawlerIntervalLabel: "Logo interval (minutes)",
    save: "Save",
    saveFailed: "Save failed",
    saving: "Saving...",
    running: "Running",
    noLogs: "No logs yet",
    crawlerSiteSaved: "Crawler site saved",
    crawlerSiteSaveFailed: "Crawler site save failed",
    addCrawlerSite: "Add Crawler Site",
    updateCrawlerSite: "Update Crawler Site",
    runCouponCrawler: "coupon crawler",
    runBrandCrawler: "brand crawler",
    runLogoCrawler: "brand logo crawler",
    addCoupon: "Add Coupon",
    addBrand: "Add Brand",
    addBlog: "Add Blog",
    uploadFailed: "Upload failed",
    chooseImageFirst: "Choose an image file first",
    chooseHeroFirst: "Choose a hero image first",
    uploadedPrefix: "Uploaded:",
    clearForm: "Clear Form",
    saveAllCoupons: "Save All Edited Coupons",
    saveAllBrands: "Save All Edited Brands",
    saveAllBlogs: "Save All Edited Blogs",
    crawlerSites: "Crawler Sites",
    crawler: "Crawler",
    couponMgmt: "Coupon Management",
    brandMgmt: "Brand Introduction Management",
    blogMgmt: "Blog Management",
    adsMgmt: "Ads Management",
    crawlerHeaders: ["ID", "Key", "Name", "Base URL", "Active", "Coupon", "Brand", "Logo", "Actions"],
    tableSave: "Save",
    tableDelete: "Delete",
    tableEdit: "Edit",
    stagedPost: "Post",
    stagedRepost: "Repost",
    stagedPosted: "Posted",
    stagedPending: "Pending",
    stagedHeaders: ["Select", "ID", "Status", "Store", "Title", "Code", "Source", "Updated", "Posted At", "Main ID", "Actions"],
    couponHeaders: ["ID", "Store", "Title", "Category", "Expires", "Code", "Clicks", "Created", "Updated", "Affiliate URL", "Logo", "Actions"],
    brandHeaders: ["ID", "Store", "Slug", "Title", "Created", "Updated", "Summary", "Official URL", "Affiliate URL", "Logo", "Hero Image", "Description", "Actions"],
    blogHeaders: ["ID", "Title", "Created", "Updated", "Summary", "Content", "Cover", "Published", "Actions"]
  },
  zh: {
    theme: "主题",
    language: "语言",
    navSite: "站点",
    tabDashboard: "仪表盘",
    tabContent: "首页内容",
    tabTheme: "主题",
    tabSeo: "SEO",
    tabCoupons: "优惠券",
    tabBrands: "品牌",
    tabBlogs: "博客",
    tabAds: "广告",
    tabCrawler: "爬虫",
    crawlerSubtabSchedule: "定时任务",
    crawlerSubtabSites: "站点管理",
    crawlerSubtabStaging: "中转队列",
    crawlerSubtabLogs: "爬虫日志",
    dashboardTitle: "仪表盘",
    statCouponsLabel: "优惠券总数",
    statBlogsLabel: "已发布博客",
    statBrandsLabel: "品牌总数",
    statCrawlerLabel: "爬虫状态",
    contentSectionTitle: "首页 Hero 与页脚内容",
    contentSectionDesc: "编辑首页文案、背景图和页脚社媒链接",
    contentThemeLabel: "主题方案",
    couponSectionDesc: "管理优惠券信息与联盟链接",
    brandSectionDesc: "用于热门店铺详情页展示",
    blogSectionDesc: "创建/编辑首页博客内容",
    adsSectionDesc: "配置滚动条广告、首页/博客广告位和 AdSense ID",
    adsCardStripTitle: "滚动条广告",
    adsCardHomeTitle: "首页广告位",
    adsCardBlogTitle: "博客广告位",
    adsCardSenseTitle: "Google AdSense",
    crawlerSectionDesc: "优惠券 / 品牌 / Logo 爬虫分模块控制",
    crawlerSitesDesc: "添加站点并按站点控制 coupon/brand/logo 抓取",
    crawlerBuiltinHint: "内置站点：RetailMeNot / SimplyCodes 也可在下方开关控制",
    crawlerLogsTitle: "爬虫日志",
    stagedCouponsTitle: "爬虫中间表队列",
    stagedCouponsDesc: "爬虫数据先写入这里，再由你手动发布到主站。",
    stagedCouponsHint: "已发布数据会有标记。",
    postSelectedStagedBtn: "发布选中项到主站",
    logLanguage: "日志语言",
    adsStripEnabledLabel: "启用滚动条广告",
    adsHomeTopEnabledLabel: "首页顶部广告",
    adsHomeMidEnabledLabel: "首页中部广告",
    adsHomeSideLeftEnabledLabel: "首页中部左侧广告",
    adsHomeSideRightEnabledLabel: "首页中部右侧广告",
    adsHomeBottomEnabledLabel: "首页底部广告",
    adsBlogTopEnabledLabel: "博客顶部广告",
    adsBlogInlineEnabledLabel: "博客文中广告",
    adsBlogBottomEnabledLabel: "博客底部广告",
    couponCrawlerEnabledLabel: "启用定时优惠券爬虫",
    couponCrawlerIntervalLabel: "优惠券间隔（分钟）",
    brandCrawlerEnabledLabel: "启用定时品牌爬虫",
    brandCrawlerIntervalLabel: "品牌间隔（分钟）",
    brandLogoCrawlerEnabledLabel: "启用定时品牌 Logo 爬虫",
    brandLogoCrawlerIntervalLabel: "Logo 间隔（分钟）",
    save: "保存",
    saveFailed: "保存失败",
    saving: "保存中...",
    running: "运行中",
    noLogs: "暂无日志",
    crawlerSiteSaved: "爬虫站点已保存",
    crawlerSiteSaveFailed: "爬虫站点保存失败",
    addCrawlerSite: "添加爬虫站点",
    updateCrawlerSite: "更新爬虫站点",
    runCouponCrawler: "优惠券爬虫",
    runBrandCrawler: "品牌爬虫",
    runLogoCrawler: "品牌 Logo 爬虫",
    addCoupon: "新增优惠券",
    addBrand: "新增品牌",
    addBlog: "新增博客",
    uploadFailed: "上传失败",
    chooseImageFirst: "请先选择图片文件",
    chooseHeroFirst: "请先选择 Hero 图片",
    uploadedPrefix: "已上传：",
    clearForm: "清空表单",
    saveAllCoupons: "保存所有已编辑优惠券",
    saveAllBrands: "保存所有已编辑品牌",
    saveAllBlogs: "保存所有已编辑博客",
    crawlerSites: "爬虫站点",
    crawler: "爬虫",
    couponMgmt: "优惠券管理",
    brandMgmt: "品牌介绍管理",
    blogMgmt: "博客管理",
    adsMgmt: "广告管理",
    crawlerHeaders: ["ID", "标识", "名称", "基础 URL", "启用", "优惠券", "品牌", "Logo", "操作"],
    tableSave: "保存",
    tableDelete: "删除",
    tableEdit: "编辑",
    stagedPost: "发布",
    stagedRepost: "重新发布",
    stagedPosted: "已发布",
    stagedPending: "未发布",
    stagedHeaders: ["选择", "ID", "状态", "店铺", "标题", "优惠码", "来源", "更新时间", "发布时间", "主站ID", "操作"],
    couponHeaders: ["ID", "店铺", "标题", "分类", "有效期", "优惠码", "点击", "创建时间", "更新时间", "联盟链接", "Logo", "操作"],
    brandHeaders: ["ID", "店铺", "Slug", "标题", "创建时间", "更新时间", "摘要", "官网链接", "联盟链接", "Logo", "主图", "描述", "操作"],
    blogHeaders: ["ID", "标题", "创建时间", "更新时间", "摘要", "内容", "封面", "发布", "操作"]
  }
};

function t(key) {
  return (I18N[currentLang] && I18N[currentLang][key]) || (I18N.en && I18N.en[key]) || key;
}

function applyAdminLanguage() {
  const set = (id, text) => {
    const el = document.getElementById(id);
    if (el) el.textContent = text;
  };
  const setCheckboxLabel = (id, text) => {
    const el = document.getElementById(id);
    if (!el) return;
    const input = el.querySelector("input[type='checkbox']");
    if (!input) {
      el.textContent = text;
      return;
    }
    while (el.firstChild) {
      el.removeChild(el.firstChild);
    }
    el.appendChild(input);
    el.appendChild(document.createTextNode(` ${text}`));
  };
  set("adminThemeLabel", t("theme"));
  set("adminLangLabel", t("language"));
  set("navSiteLink", t("navSite"));
  set("adminLogLangLabel", t("logLanguage"));
  set("tabDashboard", t("tabDashboard"));
  set("tabContent", t("tabContent"));
  set("tabTheme", t("tabTheme"));
  set("tabSeo", t("tabSeo"));
  set("tabCoupons", t("tabCoupons"));
  set("tabBrands", t("tabBrands"));
  set("tabBlogs", t("tabBlogs"));
  set("tabAds", t("tabAds"));
  set("tabCrawler", t("tabCrawler"));
  set("crawlerSubtabSchedule", t("crawlerSubtabSchedule"));
  set("crawlerSubtabSites", t("crawlerSubtabSites"));
  set("crawlerSubtabStaging", t("crawlerSubtabStaging"));
  set("crawlerSubtabLogs", t("crawlerSubtabLogs"));
  set("dashboardTitle", t("dashboardTitle"));
  set("statCouponsLabel", t("statCouponsLabel"));
  set("statBlogsLabel", t("statBlogsLabel"));
  set("statBrandsLabel", t("statBrandsLabel"));
  set("statCrawlerLabel", t("statCrawlerLabel"));
  set("contentSectionTitle", t("contentSectionTitle"));
  set("contentSectionDesc", t("contentSectionDesc"));
  set("contentThemeLabel", t("contentThemeLabel"));
  set("couponSectionDesc", t("couponSectionDesc"));
  set("brandSectionDesc", t("brandSectionDesc"));
  set("blogSectionDesc", t("blogSectionDesc"));
  set("adsSectionDesc", t("adsSectionDesc"));
  set("adsCardStripTitle", t("adsCardStripTitle"));
  set("adsCardHomeTitle", t("adsCardHomeTitle"));
  set("adsCardBlogTitle", t("adsCardBlogTitle"));
  set("adsCardSenseTitle", t("adsCardSenseTitle"));
  set("crawlerSectionDesc", t("crawlerSectionDesc"));
  set("crawlerLogsTitle", t("crawlerLogsTitle"));
  set("crawlerSitesDesc", t("crawlerSitesDesc"));
  set("crawlerBuiltinHint", t("crawlerBuiltinHint"));
  set("stagedCouponsTitle", t("stagedCouponsTitle"));
  set("stagedCouponsDesc", t("stagedCouponsDesc"));
  set("stagedCouponsHint", t("stagedCouponsHint"));
  setCheckboxLabel("adsStripEnabledLabel", t("adsStripEnabledLabel"));
  setCheckboxLabel("adsHomeTopEnabledLabel", t("adsHomeTopEnabledLabel"));
  setCheckboxLabel("adsHomeMidEnabledLabel", t("adsHomeMidEnabledLabel"));
  setCheckboxLabel("adsHomeSideLeftEnabledLabel", t("adsHomeSideLeftEnabledLabel"));
  setCheckboxLabel("adsHomeSideRightEnabledLabel", t("adsHomeSideRightEnabledLabel"));
  setCheckboxLabel("adsHomeBottomEnabledLabel", t("adsHomeBottomEnabledLabel"));
  setCheckboxLabel("adsBlogTopEnabledLabel", t("adsBlogTopEnabledLabel"));
  setCheckboxLabel("adsBlogInlineEnabledLabel", t("adsBlogInlineEnabledLabel"));
  setCheckboxLabel("adsBlogBottomEnabledLabel", t("adsBlogBottomEnabledLabel"));
  setCheckboxLabel("couponCrawlerEnabledLabel", t("couponCrawlerEnabledLabel"));
  set("couponCrawlerIntervalLabel", t("couponCrawlerIntervalLabel"));
  setCheckboxLabel("brandCrawlerEnabledLabel", t("brandCrawlerEnabledLabel"));
  set("brandCrawlerIntervalLabel", t("brandCrawlerIntervalLabel"));
  setCheckboxLabel("brandLogoCrawlerEnabledLabel", t("brandLogoCrawlerEnabledLabel"));
  set("brandLogoCrawlerIntervalLabel", t("brandLogoCrawlerIntervalLabel"));
  const couponTitleEl = document.querySelector('[data-section="coupons"] .section-head-admin h3');
  if (couponTitleEl) couponTitleEl.textContent = t("couponMgmt");
  const brandTitleEl = document.querySelector('[data-section="brands"] .section-head-admin h3');
  if (brandTitleEl) brandTitleEl.textContent = t("brandMgmt");
  const blogTitleEl = document.querySelector('[data-section="blogs"] .section-head-admin h3');
  if (blogTitleEl) blogTitleEl.textContent = t("blogMgmt");
  const adsTitleEl = document.querySelector('[data-section="ads"] .section-head-admin h3');
  if (adsTitleEl) adsTitleEl.textContent = t("adsMgmt");
  set("crawlerMainTitle", t("crawler"));
  set("crawlerSitesTitle", t("crawlerSites"));
  if (saveCrawlerBtn) saveCrawlerBtn.textContent = t("save");
  if (saveContentBtn) saveContentBtn.textContent = currentLang === "zh" ? "保存首页内容" : "Save Hero Content";
  if (uploadHeroImageBtn) uploadHeroImageBtn.textContent = currentLang === "zh" ? "上传 Hero 图片" : "Upload Hero Image";
  if (uploadImageBtn) uploadImageBtn.textContent = currentLang === "zh" ? "上传图片" : "Upload Image";
  if (saveAdsBtn) saveAdsBtn.textContent = currentLang === "zh" ? "保存广告配置" : "Save Ads Config";
  if (runCouponCrawlerBtn) runCouponCrawlerBtn.textContent = currentLang === "zh" ? "运行优惠券爬虫" : "Run Coupon Crawler";
  if (runBrandCrawlerBtn) runBrandCrawlerBtn.textContent = currentLang === "zh" ? "运行品牌爬虫" : "Run Brand Crawler";
  if (runBrandLogoCrawlerBtn) runBrandLogoCrawlerBtn.textContent = currentLang === "zh" ? "运行品牌 Logo 爬虫" : "Run Brand Logo Crawler";
  if (postSelectedStagedBtn) postSelectedStagedBtn.textContent = t("postSelectedStagedBtn");
  if (saveCrawlerSiteBtn && !saveCrawlerSiteBtn.dataset.editId) saveCrawlerSiteBtn.textContent = t("addCrawlerSite");
  if (saveCouponBtn && !saveCouponBtn.dataset.editId) saveCouponBtn.textContent = t("addCoupon");
  if (saveBrandBtn && !saveBrandBtn.dataset.editId) saveBrandBtn.textContent = t("addBrand");
  if (saveBlogBtn && !saveBlogBtn.dataset.editId) saveBlogBtn.textContent = t("addBlog");
  if (clearCouponBtn) clearCouponBtn.textContent = t("clearForm");
  if (clearBrandBtn) clearBrandBtn.textContent = t("clearForm");
  if (clearBlogBtn) clearBlogBtn.textContent = t("clearForm");
  if (saveAllCouponsBtn) saveAllCouponsBtn.textContent = t("saveAllCoupons");
  if (saveAllBrandsBtn) saveAllBrandsBtn.textContent = t("saveAllBrands");
  if (saveAllBlogsBtn) saveAllBlogsBtn.textContent = t("saveAllBlogs");
  if (logoutBtn) logoutBtn.textContent = currentLang === "zh" ? "退出登录" : "Logout";
  if (contentThemePreset && contentThemePreset.options.length >= 3) {
    contentThemePreset.options[0].text = currentLang === "zh" ? "方案 A - 信任蓝（推荐）" : "Scheme A - Trust Blue (Recommended)";
    contentThemePreset.options[1].text = currentLang === "zh" ? "方案 B - 科技深海军蓝 + 青色" : "Scheme B - Tech Navy + Cyan";
    contentThemePreset.options[2].text = currentLang === "zh" ? "方案 C - 高转化电商红" : "Scheme C - High Conversion Red";
  }
  if (quickThemePreset) {
    quickThemePreset.value = contentThemePreset?.value || "scheme-a";
  }
  if (logLang) {
    logLang.value = currentLang;
  }
}
const sortState = {
  coupons: { key: "createdAt", dir: "desc" },
  brands: { key: "createdAt", dir: "desc" },
  blogs: { key: "createdAt", dir: "desc" }
};

const dirtyCouponIds = new Set();
const dirtyBrandIds = new Set();
const dirtyBlogIds = new Set();
const tableViewState = {
  coupons: { page: 1, size: 50, q: "", from: "", to: "", extra: "all" },
  brands: { page: 1, size: 50, q: "", from: "", to: "", extra: "all" },
  blogs: { page: 1, size: 50, q: "", from: "", to: "", extra: "all" },
  crawlerSites: { page: 1, size: 50, q: "", from: "", to: "", extra: "all" },
  stagedCoupons: { page: 1, size: 50, q: "", from: "", to: "", extra: "all" }
};

function formatDateInput(value) {
  if (!value) return "";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "";
  return d.toISOString().slice(0, 10);
}

function inDateRange(value, from, to) {
  if ((!from || from === "") && (!to || to === "")) return true;
  const current = new Date(value);
  if (Number.isNaN(current.getTime())) return false;
  if (from) {
    const fromDate = new Date(`${from}T00:00:00`);
    if (current < fromDate) return false;
  }
  if (to) {
    const toDate = new Date(`${to}T23:59:59`);
    if (current > toDate) return false;
  }
  return true;
}

function paginateRows(rows, state) {
  const total = rows.length;
  const pages = Math.max(1, Math.ceil(total / state.size));
  state.page = Math.min(state.page, pages);
  const start = (state.page - 1) * state.size;
  return { rows: rows.slice(start, start + state.size), total, pages };
}

function ensureTableControls(tableId, state, options = {}) {
  const table = document.getElementById(tableId);
  if (!table) return null;
  const wrap = table.closest(".table-wrap");
  if (!wrap) return null;
  const id = `${tableId}Controls`;
  let controls = document.getElementById(id);
  if (!controls) {
    controls = document.createElement("div");
    controls.id = id;
    controls.className = "table-controls";
    wrap.parentNode.insertBefore(controls, wrap);
  }
  const extraOptions = options.extraOptions || [];
  const extraHtml = extraOptions.length
    ? `<select data-key="extra">${extraOptions.map(item => `<option value="${item.value}" ${state.extra === item.value ? "selected" : ""}>${item.label}</option>`).join("")}</select>`
    : "";
  controls.innerHTML = `
    <input type="text" placeholder="${options.searchPlaceholder || "Search"}" value="${state.q || ""}" data-key="q">
    <label>From <input type="date" value="${state.from || ""}" data-key="from"></label>
    <label>To <input type="date" value="${state.to || ""}" data-key="to"></label>
    ${extraHtml}
    <select data-key="size">
      <option value="50" ${state.size === 50 ? "selected" : ""}>50 / page</option>
      <option value="100" ${state.size === 100 ? "selected" : ""}>100 / page</option>
      <option value="200" ${state.size === 200 ? "selected" : ""}>200 / page</option>
    </select>
    <button class="admin-mini-btn" type="button" data-key="prev">Prev</button>
    <span data-key="pageInfo"></span>
    <button class="admin-mini-btn" type="button" data-key="next">Next</button>
  `;
  return controls;
}

function showTab(tab) {
  document.querySelectorAll(".admin-tab").forEach(btn => btn.classList.toggle("active", btn.dataset.tab === tab));
  document.querySelectorAll("[data-section]").forEach(section => section.classList.toggle("hidden", section.dataset.section !== tab));
}

function showCrawlerTab(tab) {
  document.querySelectorAll(".admin-subtab").forEach(btn => {
    btn.classList.toggle("active", btn.dataset.crawlerTab === tab);
  });
  document.querySelectorAll("[data-crawler-section]").forEach(section => {
    section.classList.toggle("hidden", section.dataset.crawlerSection !== tab);
  });
}

tabsRoot.addEventListener("click", event => {
  const btn = event.target.closest(".admin-tab");
  if (!btn) return;
  showTab(btn.dataset.tab);
});
if (crawlerSubTabs) {
  crawlerSubTabs.addEventListener("click", event => {
    const btn = event.target.closest(".admin-subtab");
    if (!btn) return;
    showCrawlerTab(btn.dataset.crawlerTab);
  });
}

document.addEventListener("change", event => {
  const target = event.target;
  if (!(target instanceof HTMLElement)) return;
  const controls = target.closest(".table-controls");
  if (!controls) return;
  const stateKey = controls.id.replace("Controls", "").replace("stagedCouponTable", "stagedCoupons").replace("crawlerSiteTable", "crawlerSites").replace("couponTable", "coupons").replace("brandTable", "brands").replace("blogTable", "blogs");
  const state = tableViewState[stateKey];
  if (!state) return;
  const key = target.dataset.key;
  if (!key) return;
  if (key === "size") {
    state.size = Number(target.value || 50);
    state.page = 1;
  } else if (key === "from" || key === "to" || key === "extra") {
    state[key] = target.value || "";
    state.page = 1;
  }
  if (stateKey === "coupons") renderCouponRows(cachedCoupons);
  if (stateKey === "brands") renderBrandRows(cachedBrands);
  if (stateKey === "blogs") renderBlogRows(cachedBlogs);
  if (stateKey === "crawlerSites") renderCrawlerSites(cachedCrawlerSites);
  if (stateKey === "stagedCoupons") renderStagedCouponRows(cachedStagedCoupons);
});

document.addEventListener("input", event => {
  const target = event.target;
  if (!(target instanceof HTMLElement)) return;
  const controls = target.closest(".table-controls");
  if (!controls) return;
  if (target.dataset.key !== "q") return;
  const stateKey = controls.id.replace("Controls", "").replace("stagedCouponTable", "stagedCoupons").replace("crawlerSiteTable", "crawlerSites").replace("couponTable", "coupons").replace("brandTable", "brands").replace("blogTable", "blogs");
  const state = tableViewState[stateKey];
  if (!state) return;
  state.q = target.value || "";
  state.page = 1;
  if (stateKey === "coupons") renderCouponRows(cachedCoupons);
  if (stateKey === "brands") renderBrandRows(cachedBrands);
  if (stateKey === "blogs") renderBlogRows(cachedBlogs);
  if (stateKey === "crawlerSites") renderCrawlerSites(cachedCrawlerSites);
  if (stateKey === "stagedCoupons") renderStagedCouponRows(cachedStagedCoupons);
});

document.addEventListener("click", event => {
  const target = event.target;
  if (!(target instanceof HTMLElement)) return;
  const controls = target.closest(".table-controls");
  if (!controls) return;
  const stateKey = controls.id.replace("Controls", "").replace("stagedCouponTable", "stagedCoupons").replace("crawlerSiteTable", "crawlerSites").replace("couponTable", "coupons").replace("brandTable", "brands").replace("blogTable", "blogs");
  const state = tableViewState[stateKey];
  if (!state) return;
  const key = target.dataset.key;
  if (key === "prev") {
    state.page = Math.max(1, state.page - 1);
  } else if (key === "next") {
    state.page += 1;
  } else {
    return;
  }
  if (stateKey === "coupons") renderCouponRows(cachedCoupons);
  if (stateKey === "brands") renderBrandRows(cachedBrands);
  if (stateKey === "blogs") renderBlogRows(cachedBlogs);
  if (stateKey === "crawlerSites") renderCrawlerSites(cachedCrawlerSites);
  if (stateKey === "stagedCoupons") renderStagedCouponRows(cachedStagedCoupons);
});

async function adminFetch(url, options = {}) {
  const response = await fetch(url, options);
  if (response.status === 401) {
    window.location.href = "/admin-login.html";
    throw new Error("Unauthorized");
  }
  return response;
}

async function checkAuth() {
  const response = await fetch("/api/admin/auth/status");
  if (!response.ok) {
    window.location.href = "/admin-login.html";
    return false;
  }
  const data = await response.json();
  if (!data.loggedIn) {
    window.location.href = "/admin-login.html";
    return false;
  }
  return true;
}

function normalizeColor(value) {
  if (!value) return "#f7f9fd";
  const hex = value.trim();
  if (/^#[0-9a-fA-F]{6}$/.test(hex)) return hex;
  return "#f7f9fd";
}

function applyCheckboxLabelLayout() {
  document.querySelectorAll("label").forEach(label => {
    if (label.querySelector("input[type='checkbox']")) {
      label.classList.add("checkbox-label");
    }
  });
}

function activateInlineEditing(table, dirtySet) {
  table.addEventListener("dblclick", event => {
    const cell = event.target.closest("td.editable-cell");
    if (!cell) return;
    cell.contentEditable = "true";
    cell.classList.add("is-editing");
    cell.focus();
    document.execCommand("selectAll", false, null);
  });

  table.addEventListener("keydown", event => {
    const cell = event.target.closest("td.editable-cell");
    if (!cell) return;
    if (event.key === "Enter") {
      event.preventDefault();
      cell.blur();
    }
  });

  table.addEventListener("blur", event => {
    const cell = event.target.closest("td.editable-cell");
    if (!cell) return;
    cell.contentEditable = "false";
    cell.classList.remove("is-editing");
    const row = cell.closest("tr[data-id]");
    if (!row) return;
    row.classList.add("is-dirty");
    dirtySet.add(Number(row.dataset.id));
  }, true);
}

function readRowData(row, fields) {
  const data = {};
  fields.forEach(field => {
    const cell = row.querySelector(`[data-field='${field.name}']`);
    let raw = (cell?.textContent || "").trim();
    if (field.type === "boolean") {
      raw = raw.toLowerCase() === "true";
    }
    data[field.name] = raw;
  });
  return data;
}

function toTimestamp(value) {
  if (!value) return 0;
  const t = new Date(value).getTime();
  return Number.isNaN(t) ? 0 : t;
}

function formatDateTime(value) {
  if (!value) return "-";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "-";
  return d.toLocaleString();
}

function sortedCopy(list, state, comparators = {}) {
  const arr = [...(list || [])];
  const { key, dir } = state;
  const factor = dir === "asc" ? 1 : -1;
  const custom = comparators[key];
  arr.sort((a, b) => {
    if (custom) {
      return custom(a, b) * factor;
    }
    const av = (a?.[key] ?? "").toString().toLowerCase();
    const bv = (b?.[key] ?? "").toString().toLowerCase();
    if (av < bv) return -1 * factor;
    if (av > bv) return 1 * factor;
    return 0;
  });
  return arr;
}

function sortLabel(base, state, key) {
  if (state.key !== key) return base;
  return `${base} ${state.dir === "asc" ? "▲" : "▼"}`;
}

async function loadCrawler() {
  const settings = await (await adminFetch("/api/admin/settings")).json();
  couponCrawlerEnabled.checked = settings.couponCrawlerEnabled ?? false;
  brandCrawlerEnabled.checked = settings.brandCrawlerEnabled ?? true;
  brandLogoCrawlerEnabled.checked = settings.brandLogoCrawlerEnabled ?? true;
  couponCrawlerIntervalMinutes.value = Math.max(1, Number(settings.couponCrawlerIntervalMinutes || 30));
  brandCrawlerIntervalMinutes.value = Math.max(1, Number(settings.brandCrawlerIntervalMinutes || 30));
  brandLogoCrawlerIntervalMinutes.value = Math.max(1, Number(settings.brandLogoCrawlerIntervalMinutes || 30));
  cachedCrawlerLogs = await (await adminFetch("/api/admin/logs")).json();
  renderCrawlerLogs();
  await loadCrawlerReport();
  statCrawler.textContent = [
    `Coupon crawler: ${couponCrawlerEnabled.checked ? "enabled" : "disabled"} (every ${couponCrawlerIntervalMinutes.value} min)`,
    `Brand crawler: ${brandCrawlerEnabled.checked ? "enabled" : "disabled"} (every ${brandCrawlerIntervalMinutes.value} min)`,
    `Logo crawler: ${brandLogoCrawlerEnabled.checked ? "enabled" : "disabled"} (every ${brandLogoCrawlerIntervalMinutes.value} min)`
  ].join(" | ");
  await Promise.all([loadCrawlerSites(), loadStagedCoupons()]);
}

async function loadCrawlerReport() {
  if (!crawlerReportTable) return;
  const data = await (await adminFetch("/api/admin/crawler/report")).json();
  const rows = data.sites || [];
  crawlerReportTable.innerHTML = `<thead><tr>
    <th>Site</th><th>Staged Coupons</th><th>Brands</th><th>Logos</th>
  </tr></thead><tbody>${
    rows.map(r => `<tr><td>${r.siteName || r.siteKey}</td><td>${r.stagedCouponCount ?? 0}</td><td>${r.brandCount ?? 0}</td><td>${r.logoCount ?? 0}</td></tr>`).join("")
  }</tbody><tfoot><tr><th>Total</th><th>${data.totalStagedCoupons ?? 0}</th><th>${data.totalBrands ?? 0}</th><th>${data.totalStagedLogos ?? 0}</th></tr></tfoot>`;
  await loadStagedBrandLogos();
}

async function loadStagedBrandLogos() {
  if (!stagedBrandLogoTable) return;
  const data = await (await adminFetch("/api/admin/staged-brand-logos")).json();
  stagedBrandLogoTable.innerHTML = `<thead><tr><th>ID</th><th>Store</th><th>Source Site</th><th>Source URL</th><th>Logo</th><th>Updated</th></tr></thead><tbody>${
    data.map(item => `
      <tr>
        <td>${item.id}</td>
        <td>${item.storeName || ""}</td>
        <td>${item.sourceSiteKey || ""}</td>
        <td class="cut-cell">${item.sourceUrl || ""}</td>
        <td><img src="${item.imageUrl}" alt="logo" style="width:38px;height:38px;object-fit:contain;border:1px solid #d7e3f3;border-radius:8px;padding:3px;background:#fff;"></td>
        <td>${formatDateTime(item.updatedAt)}</td>
      </tr>`).join("")
  }</tbody>`;
}

function translateLogMessage(message) {
  const map = [
    ["[source=retailmenot]", "[数据源=RetailMeNot]"],
    ["[source=simplycodes]", "[数据源=SimplyCodes]"],
    ["[source=brand-profile]", "[数据源=品牌资料]"],
    ["[source=brand-logo]", "[数据源=品牌Logo]"],
    ["[source=custom-coupon]", "[数据源=自定义优惠券站点]"],
    ["[source=custom-brand]", "[数据源=自定义品牌站点]"],
    ["[source=custom-logo]", "[数据源=自定义Logo站点]"],
    ["Coupon crawler skipped by site switch.", "优惠券爬虫已按站点开关跳过。"],
    ["Coupon crawler started.", "优惠券爬虫已启动。"],
    ["Brand profile crawler started.", "品牌资料爬虫已启动。"],
    ["Brand logo crawler started.", "品牌 Logo 爬虫已启动。"],
    ["Brand profile crawler finished.", "品牌资料爬虫已完成。"],
    ["Brand logo crawler finished.", "品牌 Logo 爬虫已完成。"],
    ["Custom coupon crawler finished.", "自定义优惠券爬虫已完成。"],
    ["Custom brand crawler finished.", "自定义品牌爬虫已完成。"],
    ["Custom logo crawler finished.", "自定义 Logo 爬虫已完成。"],
    ["Seed summary:", "种子汇总："],
    ["blocked=", "被拦截="],
    ["failed=", "失败原因="],
    ["store=", "店铺="],
    ["parsed=", "解析到="],
    ["upserts=", "新增或更新="],
    ["duplicates=", "重复="],
    ["skippedDuplicates=", "跳过重复="],
    ["scannedStores=", "扫描店铺数="],
    ["scannedSites=", "扫描站点数="],
    ["seedStores=", "种子店铺数="],
    ["logosStored=", "存储Logo数="],
    ["fallbackInserted=", "回退写入数="],
    ["newFromCoupons=", "来自优惠券新增="],
    ["popularSeeds=", "内置热门种子="],
    ["discoveredFromSimplyCodes=", "从SimplyCodes发现="],
    ["mergedUniqueStores=", "合并去重后店铺数="],
    ["url=", "链接="],
    ["fallbackUrl=", "回退链接="]
  ];
  let translated = message || "";
  map.forEach(([from, to]) => {
    translated = translated.split(from).join(to);
  });
  return translated;
}

function renderCrawlerLogs() {
  const lang = logLang?.value || currentLang;
  if (!cachedCrawlerLogs.length) {
    logPanel.textContent = lang === "zh" ? "暂无日志" : t("noLogs");
    return;
  }
  logPanel.textContent = cachedCrawlerLogs.map(log => {
    const message = lang === "zh" ? translateLogMessage(log.message) : log.message;
    return `[${log.createdAt}] [${log.level}] ${message}`;
  }).join("\n");
}

function clearCrawlerSiteForm() {
  crawlerSiteName.value = "";
  crawlerSiteBaseUrl.value = "";
  crawlerSiteActive.checked = true;
  crawlerSiteCouponEnabled.checked = true;
  crawlerSiteBrandEnabled.checked = true;
  crawlerSiteLogoEnabled.checked = true;
  saveCrawlerSiteBtn.dataset.editId = "";
  saveCrawlerSiteBtn.textContent = t("addCrawlerSite");
}

function renderCrawlerSites(rows) {
  const stateView = tableViewState.crawlerSites;
  const controls = ensureTableControls("crawlerSiteTable", stateView, {
    searchPlaceholder: "Search key/name/url"
  });
  const filteredRows = (rows || []).filter(site => {
    if (!inDateRange(site.createdAt, stateView.from, stateView.to)) return false;
    const q = (stateView.q || "").trim().toLowerCase();
    if (!q) return true;
    const merged = `${site.siteKey || ""} ${site.siteName || ""} ${site.baseUrl || ""}`.toLowerCase();
    return merged.includes(q);
  });
  const page = paginateRows(filteredRows, stateView);
  const headers = t("crawlerHeaders");
  crawlerSiteTable.innerHTML = `<thead><tr>
    <th>${headers[0]}</th><th>${headers[1]}</th><th>${headers[2]}</th><th>${headers[3]}</th><th>${headers[4]}</th><th>${headers[5]}</th><th>${headers[6]}</th><th>${headers[7]}</th><th>${headers[8]}</th>
  </tr></thead><tbody>${page.rows.map(site => `
    <tr data-id="${site.id}">
      <td>${site.id}</td>
      <td>${site.siteKey}</td>
      <td>${site.siteName}</td>
      <td class="cut-cell">${site.baseUrl}</td>
      <td><input type="checkbox" data-toggle="active" ${site.active ? "checked" : ""}></td>
      <td><input type="checkbox" data-toggle="couponEnabled" ${site.couponEnabled ? "checked" : ""}></td>
      <td><input type="checkbox" data-toggle="brandEnabled" ${site.brandEnabled ? "checked" : ""}></td>
      <td><input type="checkbox" data-toggle="logoEnabled" ${site.logoEnabled ? "checked" : ""}></td>
      <td><button class="admin-mini-btn" data-edit-site="${site.id}">${t("tableEdit")}</button> <button class="admin-mini-btn" data-del-site="${site.id}">${t("tableDelete")}</button></td>
    </tr>
  `).join("")}</tbody>`;
  if (controls) {
    const pageInfo = controls.querySelector('[data-key="pageInfo"]');
    if (pageInfo) pageInfo.textContent = `${stateView.page}/${page.pages} (${page.total})`;
  }
}

async function loadCrawlerSites() {
  cachedCrawlerSites = await (await adminFetch("/api/admin/crawler/sites")).json();
  renderCrawlerSites(cachedCrawlerSites);
}

function renderStagedCouponRows(rows) {
  if (!stagedCouponTable) return;
  const stateView = tableViewState.stagedCoupons;
  const controls = ensureTableControls("stagedCouponTable", stateView, {
    searchPlaceholder: "Search store/title/code/source",
    extraOptions: [
      { value: "all", label: "All statuses" },
      { value: "posted", label: "Posted" },
      { value: "pending", label: "Pending" }
    ]
  });
  const filteredRows = (rows || []).filter(c => {
    if (!inDateRange(c.updatedAt, stateView.from, stateView.to)) return false;
    if (stateView.extra === "posted" && !c.posted) return false;
    if (stateView.extra === "pending" && c.posted) return false;
    const q = (stateView.q || "").trim().toLowerCase();
    if (!q) return true;
    const merged = `${c.store || ""} ${c.title || ""} ${c.couponCode || ""} ${c.source || ""}`.toLowerCase();
    return merged.includes(q);
  });
  const page = paginateRows(filteredRows, stateView);
  const headers = t("stagedHeaders");
  stagedCouponTable.innerHTML = `<thead><tr>
    <th><input type="checkbox" id="selectAllStaged"></th><th>${headers[1]}</th><th>${headers[2]}</th><th>${headers[3]}</th><th>${headers[4]}</th><th>${headers[5]}</th><th>${headers[6]}</th><th>${headers[7]}</th><th>${headers[8]}</th><th>${headers[9]}</th><th>${headers[10]}</th>
  </tr></thead><tbody>${page.rows.map(c => {
    const checked = selectedStagedCouponIds.has(c.id) ? "checked" : "";
    const statusText = c.posted ? t("stagedPosted") : t("stagedPending");
    const statusClass = c.posted ? "staged-status posted" : "staged-status pending";
    return `<tr data-id="${c.id}">
      <td><input type="checkbox" data-staged-select="${c.id}" ${checked}></td>
      <td>${c.id}</td>
      <td><span class="${statusClass}">${statusText}</span></td>
      <td>${c.store || ""}</td>
      <td>${c.title || ""}</td>
      <td>${c.couponCode || ""}</td>
      <td>${c.source || ""}</td>
      <td>${formatDateTime(c.updatedAt)}</td>
      <td>${formatDateTime(c.postedAt)}</td>
      <td>${c.postedCouponId || ""}</td>
      <td><button class="admin-mini-btn" data-post-staged="${c.id}">${c.posted ? t("stagedRepost") : t("stagedPost")}</button></td>
    </tr>`;
  }).join("")}</tbody>`;
  const selectAll = stagedCouponTable.querySelector("#selectAllStaged");
  if (selectAll) {
    const pageIds = page.rows.map(item => item.id);
    selectAll.checked = pageIds.length > 0 && pageIds.every(id => selectedStagedCouponIds.has(id));
    selectAll.addEventListener("change", () => {
      pageIds.forEach(id => {
        if (selectAll.checked) {
          selectedStagedCouponIds.add(id);
        } else {
          selectedStagedCouponIds.delete(id);
        }
      });
      renderStagedCouponRows(cachedStagedCoupons);
    });
  }
  if (controls) {
    const pageInfo = controls.querySelector('[data-key="pageInfo"]');
    if (pageInfo) pageInfo.textContent = `${stateView.page}/${page.pages} (${page.total})`;
  }
}

async function loadStagedCoupons() {
  if (!stagedCouponTable) return;
  cachedStagedCoupons = await (await adminFetch("/api/admin/staged-coupons")).json();
  const validIds = new Set(cachedStagedCoupons.map(c => c.id));
  selectedStagedCouponIds = new Set(Array.from(selectedStagedCouponIds).filter(id => validIds.has(id)));
  renderStagedCouponRows(cachedStagedCoupons);
}

async function postSelectedStagedCoupons() {
  if (!selectedStagedCouponIds.size) {
    crawlerStatus.textContent = currentLang === "zh" ? "请先选择要发布的数据" : "Please select staged rows first";
    return;
  }
  const response = await adminFetch("/api/admin/staged-coupons/post-batch", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ ids: Array.from(selectedStagedCouponIds) })
  });
  crawlerStatus.textContent = await response.text();
  await Promise.all([loadStagedCoupons(), loadCoupons()]);
}

async function saveCrawlerSite() {
  const body = {
    id: saveCrawlerSiteBtn.dataset.editId ? Number(saveCrawlerSiteBtn.dataset.editId) : null,
    siteName: crawlerSiteName.value.trim(),
    baseUrl: crawlerSiteBaseUrl.value.trim(),
    active: crawlerSiteActive.checked,
    couponEnabled: crawlerSiteCouponEnabled.checked,
    brandEnabled: crawlerSiteBrandEnabled.checked,
    logoEnabled: crawlerSiteLogoEnabled.checked
  };
  const response = await adminFetch("/api/admin/crawler/sites", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  if (!response.ok) {
    crawlerStatus.textContent = t("crawlerSiteSaveFailed");
    return;
  }
  clearCrawlerSiteForm();
  await loadCrawlerSites();
  crawlerStatus.textContent = t("crawlerSiteSaved");
}

async function loadContent() {
  const data = await (await adminFetch("/api/admin/content")).json();
  contentThemePreset.value = data.themePreset || "scheme-a";
  themeDisplayName.value = data.themeDisplayName || "";
  siteName.value = data.siteName || "";
  siteSlogan.value = data.siteSlogan || "";
  siteLogoText.value = data.siteLogoText || "";
  siteLogoImageUrl.value = data.siteLogoImageUrl || "";
  if (quickThemePreset) {
    quickThemePreset.value = contentThemePreset.value;
  }
  contentHeroEyebrow.value = data.heroEyebrow || "";
  contentHeroTitle.value = data.heroTitle || "";
  contentHeroSubtitle.value = data.heroSubtitle || "";
  contentHeroBgColor.value = data.heroBgColor || "#f7f9fd";
  contentHeroBgColorPicker.value = normalizeColor(contentHeroBgColor.value);
  contentHeroBgImageUrl.value = data.heroBgImageUrl || "";
  contentFooterTagline.value = data.footerTagline || "";
  contentFooterAboutUrl.value = data.footerAboutUrl || "";
  contentFooterPrivacyUrl.value = data.footerPrivacyUrl || "";
  contentFooterContactUrl.value = data.footerContactUrl || "";
  contentFooterSubmitCouponUrl.value = data.footerSubmitCouponUrl || "";
  contentFooterAffiliateDisclosureUrl.value = data.footerAffiliateDisclosureUrl || "";
  contentFooterTwitterUrl.value = data.footerTwitterUrl || "";
  contentFooterInstagramUrl.value = data.footerInstagramUrl || "";
  contentFooterFacebookUrl.value = data.footerFacebookUrl || "";
  contentFooterYoutubeUrl.value = data.footerYoutubeUrl || "";
}

async function saveContent() {
  contentStatus.textContent = t("saving");
  const body = {
    themePreset: contentThemePreset.value || "scheme-a",
    themeDisplayName: themeDisplayName.value,
    siteName: siteName.value,
    siteSlogan: siteSlogan.value,
    siteLogoText: siteLogoText.value,
    siteLogoImageUrl: siteLogoImageUrl.value,
    heroEyebrow: contentHeroEyebrow.value,
    heroTitle: contentHeroTitle.value,
    heroSubtitle: contentHeroSubtitle.value,
    heroBgColor: normalizeColor(contentHeroBgColor.value),
    heroBgImageUrl: contentHeroBgImageUrl.value,
    footerTagline: contentFooterTagline.value,
    footerAboutUrl: contentFooterAboutUrl.value,
    footerPrivacyUrl: contentFooterPrivacyUrl.value,
    footerContactUrl: contentFooterContactUrl.value,
    footerSubmitCouponUrl: contentFooterSubmitCouponUrl.value,
    footerAffiliateDisclosureUrl: contentFooterAffiliateDisclosureUrl.value,
    footerTwitterUrl: contentFooterTwitterUrl.value,
    footerInstagramUrl: contentFooterInstagramUrl.value,
    footerFacebookUrl: contentFooterFacebookUrl.value,
    footerYoutubeUrl: contentFooterYoutubeUrl.value
  };
  const response = await adminFetch("/api/admin/content", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  contentStatus.textContent = response.ok ? t("save") : t("saveFailed");
  if (response.ok && quickThemePreset) {
    quickThemePreset.value = contentThemePreset.value;
  }
}

async function saveThemePresetOnly(themePreset) {
  contentStatus.textContent = t("saving");
  const current = await (await adminFetch("/api/admin/content")).json();
  current.themePreset = themePreset;
  const response = await adminFetch("/api/admin/content", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(current)
  });
  if (response.ok) {
    contentThemePreset.value = themePreset;
    if (quickThemePreset) {
      quickThemePreset.value = themePreset;
    }
    contentStatus.textContent = t("save");
  } else {
    contentStatus.textContent = t("saveFailed");
  }
}

async function uploadHeroImage() {
  if (!heroImageFile.files.length) {
    contentStatus.textContent = t("chooseHeroFirst");
    return;
  }
  const formData = new FormData();
  formData.append("file", heroImageFile.files[0]);
  const response = await adminFetch("/api/admin/uploads/images", { method: "POST", body: formData });
  if (!response.ok) {
    contentStatus.textContent = t("uploadFailed");
    return;
  }
  const data = await response.json();
  contentHeroBgImageUrl.value = data.url;
  contentStatus.textContent = `${t("uploadedPrefix")} ${data.url}`;
}

async function saveCrawler() {
  crawlerStatus.textContent = t("saving");
  const couponMinutes = Math.max(1, Number(couponCrawlerIntervalMinutes.value || 30));
  const brandMinutes = Math.max(1, Number(brandCrawlerIntervalMinutes.value || 30));
  const logoMinutes = Math.max(1, Number(brandLogoCrawlerIntervalMinutes.value || 30));
  const response = await adminFetch("/api/admin/settings", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      couponCrawlerEnabled: couponCrawlerEnabled.checked,
      brandCrawlerEnabled: brandCrawlerEnabled.checked,
      brandLogoCrawlerEnabled: brandLogoCrawlerEnabled.checked,
      couponCrawlerIntervalMinutes: couponMinutes,
      brandCrawlerIntervalMinutes: brandMinutes,
      brandLogoCrawlerIntervalMinutes: logoMinutes
    })
  });
  crawlerStatus.textContent = response.ok ? t("save") : t("saveFailed");
  await loadCrawler();
}

async function runCrawler(endpoint, title) {
  crawlerStatus.textContent = `${t("running")} ${title}...`;
  const response = await adminFetch(endpoint, { method: "POST" });
  crawlerStatus.textContent = await response.text();
  await Promise.all([loadCrawler(), loadCoupons(), loadBrands(), loadStagedCoupons()]);
}

function clearCouponForm() {
  saveCouponBtn.dataset.editId = "";
  saveCouponBtn.textContent = t("addCoupon");
  [couponStore, couponTitle, couponCategory, couponExpires, couponCode, couponAffiliate, couponLogo].forEach(el => el.value = "");
}

async function saveCouponRowById(id) {
  const row = couponTable.querySelector(`tr[data-id='${id}']`);
  if (!row) return;
  const fields = [
    { name: "store" }, { name: "title" }, { name: "category" }, { name: "expires" },
    { name: "couponCode" }, { name: "affiliateUrl" }, { name: "logoUrl" }, { name: "clickCount" }
  ];
  const payload = { id, ...readRowData(row, fields) };
  await adminFetch("/api/admin/coupons", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  row.classList.remove("is-dirty");
  dirtyCouponIds.delete(id);
}

function renderCouponRows(coupons) {
  const stateView = tableViewState.coupons;
  const controls = ensureTableControls("couponTable", stateView, {
    searchPlaceholder: "Search store/title/category/code",
    extraOptions: [
      { value: "all", label: "All statuses" },
      { value: "active", label: "Active" },
      { value: "expired", label: "Expired" }
    ]
  });
  const filteredCoupons = (coupons || []).filter(c => {
    const q = (stateView.q || "").trim().toLowerCase();
    const isExpired = !!c.expired || (c.expires && c.expires < new Date().toISOString().slice(0, 10));
    if (stateView.extra === "active" && isExpired) return false;
    if (stateView.extra === "expired" && !isExpired) return false;
    if (!inDateRange(c.createdAt, stateView.from, stateView.to)) return false;
    if (!q) return true;
    const merged = `${c.store || ""} ${c.title || ""} ${c.category || ""} ${c.couponCode || ""}`.toLowerCase();
    return merged.includes(q);
  });
  const headers = t("couponHeaders");
  const state = sortState.coupons;
  const rows = sortedCopy(filteredCoupons, state, {
    id: (a, b) => Number(a.id || 0) - Number(b.id || 0),
    clickCount: (a, b) => Number(a.clickCount || 0) - Number(b.clickCount || 0),
    createdAt: (a, b) => toTimestamp(a.createdAt) - toTimestamp(b.createdAt),
    updatedAt: (a, b) => toTimestamp(a.updatedAt) - toTimestamp(b.updatedAt)
  });
  const page = paginateRows(rows, stateView);
  couponTable.innerHTML = `<thead><tr>
    <th data-sort="id">${sortLabel(headers[0], state, "id")}</th>
    <th data-sort="store">${sortLabel(headers[1], state, "store")}</th>
    <th data-sort="title">${sortLabel(headers[2], state, "title")}</th>
    <th data-sort="category">${sortLabel(headers[3], state, "category")}</th>
    <th data-sort="expires">${sortLabel(headers[4], state, "expires")}</th>
    <th data-sort="couponCode">${sortLabel(headers[5], state, "couponCode")}</th>
    <th data-sort="clickCount">${sortLabel(headers[6], state, "clickCount")}</th>
    <th data-sort="createdAt">${sortLabel(headers[7], state, "createdAt")}</th>
    <th data-sort="updatedAt">${sortLabel(headers[8], state, "updatedAt")}</th>
    <th>${headers[9]}</th><th>${headers[10]}</th><th>${headers[11]}</th>
  </tr></thead><tbody>${page.rows.map(c => `
    <tr data-id='${c.id}'>
      <td>${c.id}</td>
      <td class='editable-cell' data-field='store'>${c.store || ""}</td>
      <td class='editable-cell' data-field='title'>${c.title || ""}</td>
      <td class='editable-cell' data-field='category'>${c.category || ""}</td>
      <td class='editable-cell' data-field='expires'>${c.expires || ""}</td>
      <td class='editable-cell' data-field='couponCode'>${c.couponCode || ""}</td>
      <td class='editable-cell' data-field='clickCount'>${c.clickCount ?? 0}</td>
      <td>${formatDateTime(c.createdAt)}</td>
      <td>${formatDateTime(c.updatedAt)}</td>
      <td class='editable-cell cut-cell' data-field='affiliateUrl'>${c.affiliateUrl || ""}</td>
      <td class='editable-cell cut-cell' data-field='logoUrl'>${c.logoUrl || ""}</td>
      <td><button class='admin-mini-btn' data-save-coupon='${c.id}'>${t("tableSave")}</button> <button class='admin-mini-btn' data-del-coupon='${c.id}'>${t("tableDelete")}</button></td>
  </tr>`).join("")}</tbody>`;
  if (controls) {
    const pageInfo = controls.querySelector('[data-key="pageInfo"]');
    if (pageInfo) pageInfo.textContent = `${stateView.page}/${page.pages} (${page.total})`;
  }
}

async function loadCoupons() {
  cachedCoupons = await (await adminFetch("/api/admin/coupons")).json();
  renderCouponRows(cachedCoupons);
  statCoupons.textContent = String(cachedCoupons.length);
}

async function saveCoupon() {
  const body = {
    id: saveCouponBtn.dataset.editId ? Number(saveCouponBtn.dataset.editId) : null,
    store: couponStore.value,
    title: couponTitle.value,
    category: couponCategory.value,
    expires: couponExpires.value,
    couponCode: couponCode.value,
    affiliateUrl: couponAffiliate.value,
    logoUrl: couponLogo.value
  };
  await adminFetch("/api/admin/coupons", { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
  clearCouponForm();
  await loadCoupons();
}

function clearBrandForm() {
  saveBrandBtn.dataset.editId = "";
  saveBrandBtn.textContent = t("addBrand");
  [brandStoreName, brandSlug, brandTitle, brandSummary, brandOfficialUrl, brandAffiliateUrl, brandLogoUrl, brandHeroImageUrl, brandDescription].forEach(el => el.value = "");
}

async function saveBrandRowById(id) {
  const row = brandTable.querySelector(`tr[data-id='${id}']`);
  if (!row) return;
  const fields = [
    { name: "storeName" }, { name: "slug" }, { name: "title" }, { name: "summary" },
    { name: "officialUrl" }, { name: "affiliateUrl" }, { name: "logoUrl" }, { name: "heroImageUrl" }, { name: "description" }
  ];
  const payload = { id, ...readRowData(row, fields) };
  await adminFetch("/api/admin/brands", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  row.classList.remove("is-dirty");
  dirtyBrandIds.delete(id);
}

function renderBrandRows(brands) {
  const stateView = tableViewState.brands;
  const controls = ensureTableControls("brandTable", stateView, {
    searchPlaceholder: "Search store/slug/title"
  });
  const filteredBrands = (brands || []).filter(b => {
    if (!inDateRange(b.createdAt, stateView.from, stateView.to)) return false;
    const q = (stateView.q || "").trim().toLowerCase();
    if (!q) return true;
    const merged = `${b.storeName || ""} ${b.slug || ""} ${b.title || ""}`.toLowerCase();
    return merged.includes(q);
  });
  const headers = t("brandHeaders");
  const state = sortState.brands;
  const rows = sortedCopy(filteredBrands, state, {
    id: (a, b) => Number(a.id || 0) - Number(b.id || 0),
    createdAt: (a, b) => toTimestamp(a.createdAt) - toTimestamp(b.createdAt),
    updatedAt: (a, b) => toTimestamp(a.updatedAt) - toTimestamp(b.updatedAt)
  });
  const page = paginateRows(rows, stateView);
  brandTable.innerHTML = `<thead><tr>
    <th data-sort="id">${sortLabel(headers[0], state, "id")}</th>
    <th data-sort="storeName">${sortLabel(headers[1], state, "storeName")}</th>
    <th data-sort="slug">${sortLabel(headers[2], state, "slug")}</th>
    <th data-sort="title">${sortLabel(headers[3], state, "title")}</th>
    <th data-sort="createdAt">${sortLabel(headers[4], state, "createdAt")}</th>
    <th data-sort="updatedAt">${sortLabel(headers[5], state, "updatedAt")}</th>
    <th>${headers[6]}</th><th>${headers[7]}</th><th>${headers[8]}</th><th>${headers[9]}</th><th>${headers[10]}</th><th>${headers[11]}</th><th>${headers[12]}</th>
  </tr></thead><tbody>${page.rows.map(b => `
    <tr data-id='${b.id}'>
      <td>${b.id}</td>
      <td class='editable-cell' data-field='storeName'>${b.storeName || ""}</td>
      <td class='editable-cell' data-field='slug'>${b.slug || ""}</td>
      <td class='editable-cell' data-field='title'>${b.title || ""}</td>
      <td>${formatDateTime(b.createdAt)}</td>
      <td>${formatDateTime(b.updatedAt)}</td>
      <td class='editable-cell' data-field='summary'>${b.summary || ""}</td>
      <td class='editable-cell cut-cell' data-field='officialUrl'>${b.officialUrl || ""}</td>
      <td class='editable-cell cut-cell' data-field='affiliateUrl'>${b.affiliateUrl || ""}</td>
      <td class='editable-cell cut-cell' data-field='logoUrl'>${b.logoUrl || ""}</td>
      <td class='editable-cell cut-cell' data-field='heroImageUrl'>${b.heroImageUrl || ""}</td>
      <td class='editable-cell cut-cell' data-field='description'>${b.description || ""}</td>
      <td><button class='admin-mini-btn' data-save-brand='${b.id}'>${t("tableSave")}</button> <button class='admin-mini-btn' data-del-brand='${b.id}'>${t("tableDelete")}</button></td>
  </tr>`).join("")}</tbody>`;
  if (controls) {
    const pageInfo = controls.querySelector('[data-key="pageInfo"]');
    if (pageInfo) pageInfo.textContent = `${stateView.page}/${page.pages} (${page.total})`;
  }
}

async function loadBrands() {
  cachedBrands = await (await adminFetch("/api/admin/brands")).json();
  renderBrandRows(cachedBrands);
  statBrands.textContent = String(cachedBrands.length);
}

async function saveBrand() {
  const body = {
    id: saveBrandBtn.dataset.editId ? Number(saveBrandBtn.dataset.editId) : null,
    storeName: brandStoreName.value,
    slug: brandSlug.value,
    title: brandTitle.value,
    summary: brandSummary.value,
    description: brandDescription.value,
    heroImageUrl: brandHeroImageUrl.value,
    logoUrl: brandLogoUrl.value,
    officialUrl: brandOfficialUrl.value,
    affiliateUrl: brandAffiliateUrl.value
  };
  await adminFetch("/api/admin/brands", { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
  clearBrandForm();
  await loadBrands();
}

function clearBlogForm() {
  saveBlogBtn.dataset.editId = "";
  saveBlogBtn.textContent = t("addBlog");
  [blogTitle, blogSummary, blogCover, blogContent].forEach(el => el.value = "");
  if (blogContentEditor) {
    blogContentEditor.innerHTML = "";
  }
  blogPublished.value = "true";
}

async function saveBlogRowById(id) {
  const row = blogTable.querySelector(`tr[data-id='${id}']`);
  if (!row) return;
  const fields = [
    { name: "title" }, { name: "summary" }, { name: "content" }, { name: "coverImageUrl" }, { name: "published", type: "boolean" }
  ];
  const payload = { id, ...readRowData(row, fields) };
  await adminFetch("/api/admin/blogs", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  row.classList.remove("is-dirty");
  dirtyBlogIds.delete(id);
}

function renderBlogRows(blogs) {
  const stateView = tableViewState.blogs;
  const controls = ensureTableControls("blogTable", stateView, {
    searchPlaceholder: "Search title/summary/content",
    extraOptions: [
      { value: "all", label: "All statuses" },
      { value: "published", label: "Published" },
      { value: "draft", label: "Draft" }
    ]
  });
  const filteredBlogs = (blogs || []).filter(b => {
    if (!inDateRange(b.createdAt, stateView.from, stateView.to)) return false;
    if (stateView.extra === "published" && !b.published) return false;
    if (stateView.extra === "draft" && b.published) return false;
    const q = (stateView.q || "").trim().toLowerCase();
    if (!q) return true;
    const merged = `${b.title || ""} ${b.summary || ""} ${b.content || ""}`.toLowerCase();
    return merged.includes(q);
  });
  const headers = t("blogHeaders");
  const state = sortState.blogs;
  const rows = sortedCopy(filteredBlogs, state, {
    id: (a, b) => Number(a.id || 0) - Number(b.id || 0),
    createdAt: (a, b) => toTimestamp(a.createdAt) - toTimestamp(b.createdAt),
    updatedAt: (a, b) => toTimestamp(a.updatedAt) - toTimestamp(b.updatedAt)
  });
  const page = paginateRows(rows, stateView);
  blogTable.innerHTML = `<thead><tr>
    <th data-sort="id">${sortLabel(headers[0], state, "id")}</th>
    <th data-sort="title">${sortLabel(headers[1], state, "title")}</th>
    <th data-sort="createdAt">${sortLabel(headers[2], state, "createdAt")}</th>
    <th data-sort="updatedAt">${sortLabel(headers[3], state, "updatedAt")}</th>
    <th>${headers[4]}</th><th>${headers[5]}</th><th>${headers[6]}</th><th>${headers[7]}</th><th>${headers[8]}</th>
  </tr></thead><tbody>${page.rows.map(b => `
    <tr data-id='${b.id}'>
      <td>${b.id}</td>
      <td class='editable-cell' data-field='title'>${b.title || ""}</td>
      <td>${formatDateTime(b.createdAt)}</td>
      <td>${formatDateTime(b.updatedAt)}</td>
      <td class='editable-cell' data-field='summary'>${b.summary || ""}</td>
      <td class='editable-cell cut-cell' data-field='content'>${b.content || ""}</td>
      <td class='editable-cell cut-cell' data-field='coverImageUrl'>${b.coverImageUrl || ""}</td>
      <td class='editable-cell' data-field='published'>${b.published}</td>
      <td><button class='admin-mini-btn' data-save-blog='${b.id}'>${t("tableSave")}</button> <button class='admin-mini-btn' data-del-blog='${b.id}'>${t("tableDelete")}</button></td>
  </tr>`).join("")}</tbody>`;
  if (controls) {
    const pageInfo = controls.querySelector('[data-key="pageInfo"]');
    if (pageInfo) pageInfo.textContent = `${stateView.page}/${page.pages} (${page.total})`;
  }
}

async function loadBlogs() {
  cachedBlogs = await (await adminFetch("/api/admin/blogs")).json();
  renderBlogRows(cachedBlogs);
  statBlogs.textContent = String(cachedBlogs.filter(x => x.published).length);
}

async function saveBlog() {
  if (blogContentEditor) {
    blogContent.value = blogContentEditor.innerHTML;
  }
  const body = {
    id: saveBlogBtn.dataset.editId ? Number(saveBlogBtn.dataset.editId) : null,
    title: blogTitle.value,
    summary: blogSummary.value,
    content: blogContent.value,
    coverImageUrl: blogCover.value,
    published: blogPublished.value === "true"
  };
  await adminFetch("/api/admin/blogs", { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
  clearBlogForm();
  await loadBlogs();
}

function initBlogEditor() {
  if (!blogContentEditor || !blogEditorToolbar) return;
  blogContentEditor.innerHTML = blogContent.value || "";
  blogContentEditor.addEventListener("input", () => {
    blogContent.value = blogContentEditor.innerHTML;
  });
  blogEditorToolbar.addEventListener("click", event => {
    const btn = event.target.closest("[data-editor-cmd]");
    if (!btn) return;
    const cmd = btn.dataset.editorCmd;
    if (cmd === "createLink") {
      const url = window.prompt("Enter URL");
      if (url) document.execCommand("createLink", false, url);
      return;
    }
    if (cmd === "insertHTML") {
      const html = window.prompt("Insert HTML");
      if (html) document.execCommand("insertHTML", false, html);
      return;
    }
    if (cmd === "fontSize") {
      const size = window.prompt("Font size 1-7", "3");
      if (size) document.execCommand("fontSize", false, size);
      return;
    }
    if (cmd === "foreColor" || cmd === "hiliteColor") {
      const color = window.prompt("Hex color, e.g. #ff0000", "#1f2937");
      if (color) document.execCommand(cmd, false, color);
      return;
    }
    document.execCommand(cmd, false);
    blogContent.value = blogContentEditor.innerHTML;
  });
}

async function uploadBlogImage() {
  if (!blogImageFile.files.length) {
    blogStatus.textContent = t("chooseImageFirst");
    return;
  }
  const formData = new FormData();
  formData.append("file", blogImageFile.files[0]);
  const response = await adminFetch("/api/admin/uploads/images", { method: "POST", body: formData });
  if (!response.ok) {
    blogStatus.textContent = t("uploadFailed");
    return;
  }
  const data = await response.json();
  blogCover.value = data.url;
  blogStatus.textContent = `${t("uploadedPrefix")} ${data.url}`;
}

async function loadAds() {
  const data = await (await adminFetch("/api/admin/ads")).json();
  adsStripEnabled.checked = data.stripEnabled;
  adsStripText.value = data.stripText || "";
  adsStripLink.value = data.stripLink || "";
  adsHomeTopEnabled.checked = data.homeTopEnabled;
  adsHomeMidEnabled.checked = data.homeMidEnabled;
  adsHomeSideLeftEnabled.checked = data.homeSideLeftEnabled;
  adsHomeSideRightEnabled.checked = data.homeSideRightEnabled;
  adsHomeBottomEnabled.checked = data.homeBottomEnabled;
  adsBlogTopEnabled.checked = data.blogTopEnabled;
  adsBlogInlineEnabled.checked = data.blogInlineEnabled;
  adsBlogBottomEnabled.checked = data.blogBottomEnabled;
  adsenseClientId.value = data.adsenseClientId || "";
  adsenseHomeSlot.value = data.homeAdsenseSlot || "";
  adsenseBlogSlot.value = data.blogAdsenseSlot || "";
}

async function saveAds() {
  adsStatus.textContent = t("saving");
  const body = {
    stripEnabled: adsStripEnabled.checked,
    stripText: adsStripText.value,
    stripLink: adsStripLink.value,
    homeTopEnabled: adsHomeTopEnabled.checked,
    homeMidEnabled: adsHomeMidEnabled.checked,
    homeSideLeftEnabled: adsHomeSideLeftEnabled.checked,
    homeSideRightEnabled: adsHomeSideRightEnabled.checked,
    homeBottomEnabled: adsHomeBottomEnabled.checked,
    blogTopEnabled: adsBlogTopEnabled.checked,
    blogInlineEnabled: adsBlogInlineEnabled.checked,
    blogBottomEnabled: adsBlogBottomEnabled.checked,
    adsenseClientId: adsenseClientId.value,
    homeAdsenseSlot: adsenseHomeSlot.value,
    blogAdsenseSlot: adsenseBlogSlot.value
  };
  const response = await adminFetch("/api/admin/ads", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  adsStatus.textContent = response.ok ? t("save") : t("saveFailed");
}

async function loadSeo() {
  const data = await (await adminFetch("/api/admin/seo")).json();
  seoTitle.value = data.title || "";
  seoDescription.value = data.description || "";
  seoKeywords.value = data.keywords || "";
  seoOgImageUrl.value = data.ogImageUrl || "";
  seoCanonicalBase.value = data.canonicalBaseUrl || "";
}

async function saveSeo() {
  seoStatus.textContent = t("saving");
  const response = await adminFetch("/api/admin/seo", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      title: seoTitle.value,
      description: seoDescription.value,
      keywords: seoKeywords.value,
      ogImageUrl: seoOgImageUrl.value,
      canonicalBaseUrl: seoCanonicalBase.value
    })
  });
  seoStatus.textContent = response.ok ? t("save") : t("saveFailed");
}

async function loadDashboard() {
  const data = await (await adminFetch("/api/admin/dashboard")).json();
  if (data.crawlerStatusText) {
    statCrawler.textContent = data.crawlerStatusText;
  }
  const rows = data.brandCouponStats || [];
  if (!brandCouponStatTable) return;
  brandCouponStatTable.innerHTML = `<thead><tr><th>Brand</th><th>Coupons</th></tr></thead><tbody>${
    rows.map(row => `<tr><td>${row.storeName || ""}</td><td>${row.couponCount ?? 0}</td></tr>`).join("")
  }</tbody>`;
}

async function saveTheme() {
  themeStatus.textContent = t("saving");
  await saveContent();
  themeStatus.textContent = t("save");
}

async function uploadSiteLogo() {
  if (!siteLogoImageFile || !siteLogoImageFile.files.length) {
    themeStatus.textContent = t("chooseImageFirst");
    return;
  }
  const formData = new FormData();
  formData.append("file", siteLogoImageFile.files[0]);
  const response = await adminFetch("/api/admin/uploads/images", { method: "POST", body: formData });
  if (!response.ok) {
    themeStatus.textContent = t("uploadFailed");
    return;
  }
  const data = await response.json();
  siteLogoImageUrl.value = data.url;
  themeStatus.textContent = `${t("uploadedPrefix")} ${data.url}`;
}

async function logout() {
  await adminFetch("/api/admin/auth/logout", { method: "POST" });
  window.location.href = "/admin-login.html";
}

couponTable.addEventListener("click", async event => {
  const sortTh = event.target.closest("th[data-sort]");
  if (sortTh) {
    const key = sortTh.dataset.sort;
    const state = sortState.coupons;
    state.dir = state.key === key ? (state.dir === "asc" ? "desc" : "asc") : "asc";
    state.key = key;
    renderCouponRows(cachedCoupons);
    return;
  }
  const saveBtn = event.target.closest("[data-save-coupon]");
  if (saveBtn) {
    await saveCouponRowById(Number(saveBtn.dataset.saveCoupon));
    await loadCoupons();
    return;
  }
  const delBtn = event.target.closest("[data-del-coupon]");
  if (delBtn) {
    await adminFetch(`/api/admin/coupons?id=${delBtn.dataset.delCoupon}`, { method: "DELETE" });
    await loadCoupons();
  }
});

brandTable.addEventListener("click", async event => {
  const sortTh = event.target.closest("th[data-sort]");
  if (sortTh) {
    const key = sortTh.dataset.sort;
    const state = sortState.brands;
    state.dir = state.key === key ? (state.dir === "asc" ? "desc" : "asc") : "asc";
    state.key = key;
    renderBrandRows(cachedBrands);
    return;
  }
  const saveBtn = event.target.closest("[data-save-brand]");
  if (saveBtn) {
    await saveBrandRowById(Number(saveBtn.dataset.saveBrand));
    await loadBrands();
    return;
  }
  const delBtn = event.target.closest("[data-del-brand]");
  if (delBtn) {
    await adminFetch(`/api/admin/brands?id=${delBtn.dataset.delBrand}`, { method: "DELETE" });
    await loadBrands();
  }
});

blogTable.addEventListener("click", async event => {
  const sortTh = event.target.closest("th[data-sort]");
  if (sortTh) {
    const key = sortTh.dataset.sort;
    const state = sortState.blogs;
    state.dir = state.key === key ? (state.dir === "asc" ? "desc" : "asc") : "asc";
    state.key = key;
    renderBlogRows(cachedBlogs);
    return;
  }
  const saveBtn = event.target.closest("[data-save-blog]");
  if (saveBtn) {
    await saveBlogRowById(Number(saveBtn.dataset.saveBlog));
    await loadBlogs();
    return;
  }
  const delBtn = event.target.closest("[data-del-blog]");
  if (delBtn) {
    await adminFetch(`/api/admin/blogs?id=${delBtn.dataset.delBlog}`, { method: "DELETE" });
    await loadBlogs();
  }
});

crawlerSiteTable.addEventListener("click", async event => {
  const editBtn = event.target.closest("[data-edit-site]");
  if (editBtn) {
    const id = Number(editBtn.dataset.editSite);
    const site = cachedCrawlerSites.find(item => item.id === id);
    if (!site) return;
    crawlerSiteName.value = site.siteName || "";
    crawlerSiteBaseUrl.value = site.baseUrl || "";
    crawlerSiteActive.checked = !!site.active;
    crawlerSiteCouponEnabled.checked = !!site.couponEnabled;
    crawlerSiteBrandEnabled.checked = !!site.brandEnabled;
    crawlerSiteLogoEnabled.checked = !!site.logoEnabled;
    saveCrawlerSiteBtn.dataset.editId = String(site.id);
    saveCrawlerSiteBtn.textContent = t("updateCrawlerSite");
    return;
  }

  const delBtn = event.target.closest("[data-del-site]");
  if (delBtn) {
    await adminFetch(`/api/admin/crawler/sites?id=${delBtn.dataset.delSite}`, { method: "DELETE" });
    await loadCrawlerSites();
    return;
  }

  const checkbox = event.target.closest("input[data-toggle]");
  if (!checkbox) return;
  const row = checkbox.closest("tr[data-id]");
  if (!row) return;
  const id = Number(row.dataset.id);
  const site = cachedCrawlerSites.find(item => item.id === id);
  if (!site) return;
  site[checkbox.dataset.toggle] = checkbox.checked;
  await adminFetch("/api/admin/crawler/sites", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(site)
  });
  await loadCrawlerSites();
});

if (stagedCouponTable) {
  stagedCouponTable.addEventListener("click", async event => {
    const postBtn = event.target.closest("[data-post-staged]");
    if (postBtn) {
      const id = Number(postBtn.dataset.postStaged);
      const response = await adminFetch(`/api/admin/staged-coupons/post?id=${id}`, { method: "POST" });
      if (response.ok) {
        crawlerStatus.textContent = currentLang === "zh" ? `已发布中间数据 #${id}` : `Posted staged coupon #${id}`;
      }
      await Promise.all([loadStagedCoupons(), loadCoupons()]);
      return;
    }

    const checkbox = event.target.closest("input[data-staged-select]");
    if (!checkbox) return;
    const id = Number(checkbox.dataset.stagedSelect);
    if (checkbox.checked) {
      selectedStagedCouponIds.add(id);
    } else {
      selectedStagedCouponIds.delete(id);
    }
  });
}

async function saveAllCoupons() {
  for (const id of Array.from(dirtyCouponIds)) {
    await saveCouponRowById(id);
  }
  await loadCoupons();
}

async function saveAllBrands() {
  for (const id of Array.from(dirtyBrandIds)) {
    await saveBrandRowById(id);
  }
  await loadBrands();
}

async function saveAllBlogs() {
  for (const id of Array.from(dirtyBlogIds)) {
    await saveBlogRowById(id);
  }
  await loadBlogs();
}

saveCrawlerBtn.addEventListener("click", saveCrawler);
saveCrawlerSiteBtn.addEventListener("click", saveCrawlerSite);
runCouponCrawlerBtn.addEventListener("click", () => runCrawler("/api/admin/crawler/run-coupons", t("runCouponCrawler")));
runBrandCrawlerBtn.addEventListener("click", () => runCrawler("/api/admin/crawler/run-brands", t("runBrandCrawler")));
runBrandLogoCrawlerBtn.addEventListener("click", () => runCrawler("/api/admin/crawler/run-brand-logos", t("runLogoCrawler")));
if (postSelectedStagedBtn) {
  postSelectedStagedBtn.addEventListener("click", postSelectedStagedCoupons);
}
saveContentBtn.addEventListener("click", saveContent);
uploadHeroImageBtn.addEventListener("click", uploadHeroImage);
saveCouponBtn.addEventListener("click", saveCoupon);
clearCouponBtn.addEventListener("click", clearCouponForm);
saveAllCouponsBtn.addEventListener("click", saveAllCoupons);
saveBrandBtn.addEventListener("click", saveBrand);
clearBrandBtn.addEventListener("click", clearBrandForm);
saveAllBrandsBtn.addEventListener("click", saveAllBrands);
saveBlogBtn.addEventListener("click", saveBlog);
clearBlogBtn.addEventListener("click", clearBlogForm);
saveAllBlogsBtn.addEventListener("click", saveAllBlogs);
uploadImageBtn.addEventListener("click", uploadBlogImage);
saveAdsBtn.addEventListener("click", saveAds);
if (saveThemeBtn) saveThemeBtn.addEventListener("click", saveTheme);
if (uploadSiteLogoBtn) uploadSiteLogoBtn.addEventListener("click", uploadSiteLogo);
if (saveSeoBtn) saveSeoBtn.addEventListener("click", saveSeo);
logoutBtn.addEventListener("click", event => { event.preventDefault(); logout(); });
if (logLang) {
  logLang.addEventListener("change", () => {
    currentLang = logLang.value || currentLang;
    if (adminLang) {
      adminLang.value = currentLang;
    }
    localStorage.setItem("admin.lang", currentLang);
    applyAdminLanguage();
    renderCrawlerLogs();
    renderCouponRows(cachedCoupons);
    renderBrandRows(cachedBrands);
    renderBlogRows(cachedBlogs);
    renderCrawlerSites(cachedCrawlerSites);
    renderStagedCouponRows(cachedStagedCoupons);
  });
}
if (adminLang) {
  adminLang.value = currentLang;
  adminLang.addEventListener("change", () => {
    currentLang = adminLang.value || "en";
    localStorage.setItem("admin.lang", currentLang);
    if (logLang) {
      logLang.value = currentLang;
    }
    applyAdminLanguage();
    renderCrawlerLogs();
    renderCouponRows(cachedCoupons);
    renderBrandRows(cachedBrands);
    renderBlogRows(cachedBlogs);
    renderCrawlerSites(cachedCrawlerSites);
    renderStagedCouponRows(cachedStagedCoupons);
  });
}
if (quickThemePreset) {
  quickThemePreset.addEventListener("change", async () => {
    await saveThemePresetOnly(quickThemePreset.value || "scheme-a");
  });
}

contentHeroBgColorPicker.addEventListener("input", () => {
  contentHeroBgColor.value = contentHeroBgColorPicker.value;
});
contentHeroBgColor.addEventListener("input", () => {
  contentHeroBgColorPicker.value = normalizeColor(contentHeroBgColor.value);
});

activateInlineEditing(couponTable, dirtyCouponIds);
activateInlineEditing(brandTable, dirtyBrandIds);
activateInlineEditing(blogTable, dirtyBlogIds);

(async function init() {
  applyCheckboxLabelLayout();
  showCrawlerTab("schedule");
  initBlogEditor();
  applyAdminLanguage();
  const ok = await checkAuth();
  if (!ok) return;
  await Promise.all([loadCrawler(), loadContent(), loadCoupons(), loadBrands(), loadBlogs(), loadAds(), loadSeo(), loadDashboard()]);
  clearCrawlerSiteForm();
  applyAdminLanguage();
})();




