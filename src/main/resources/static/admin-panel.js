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
const logPanel = document.getElementById("logPanel");
const logLang = document.getElementById("logLang");
const couponTable = document.getElementById("couponTable");
const blogTable = document.getElementById("blogTable");
const brandTable = document.getElementById("brandTable");
const logoutBtn = document.getElementById("logoutBtn");
const tabsRoot = document.getElementById("adminTabs");

const statCoupons = document.getElementById("statCoupons");
const statBlogs = document.getElementById("statBlogs");
const statBrands = document.getElementById("statBrands");
const statCrawler = document.getElementById("statCrawler");

const contentHeroEyebrow = document.getElementById("contentHeroEyebrow");
const contentHeroTitle = document.getElementById("contentHeroTitle");
const contentHeroSubtitle = document.getElementById("contentHeroSubtitle");
const contentHeroBgColor = document.getElementById("contentHeroBgColor");
const contentHeroBgColorPicker = document.getElementById("contentHeroBgColorPicker");
const contentHeroBgImageUrl = document.getElementById("contentHeroBgImageUrl");
const contentThemePreset = document.getElementById("contentThemePreset");
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
const sortState = {
  coupons: { key: "id", dir: "asc" },
  brands: { key: "id", dir: "asc" },
  blogs: { key: "id", dir: "asc" }
};

const dirtyCouponIds = new Set();
const dirtyBrandIds = new Set();
const dirtyBlogIds = new Set();

function showTab(tab) {
  document.querySelectorAll(".admin-tab").forEach(btn => btn.classList.toggle("active", btn.dataset.tab === tab));
  document.querySelectorAll("[data-section]").forEach(section => section.classList.toggle("hidden", section.dataset.section !== tab));
}

tabsRoot.addEventListener("click", event => {
  const btn = event.target.closest(".admin-tab");
  if (!btn) return;
  showTab(btn.dataset.tab);
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
  statCrawler.textContent = [
    `C:${couponCrawlerEnabled.checked ? "on" : "off"}(${couponCrawlerIntervalMinutes.value}m)`,
    `B:${brandCrawlerEnabled.checked ? "on" : "off"}(${brandCrawlerIntervalMinutes.value}m)`,
    `L:${brandLogoCrawlerEnabled.checked ? "on" : "off"}(${brandLogoCrawlerIntervalMinutes.value}m)`
  ].join(" ");
  await loadCrawlerSites();
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
  const lang = logLang?.value || "en";
  if (!cachedCrawlerLogs.length) {
    logPanel.textContent = lang === "zh" ? "暂无日志" : "No logs yet";
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
  saveCrawlerSiteBtn.textContent = "Add Crawler Site";
}

function renderCrawlerSites(rows) {
  crawlerSiteTable.innerHTML = `<thead><tr>
    <th>ID</th><th>Key</th><th>Name</th><th>Base URL</th><th>Active</th><th>Coupon</th><th>Brand</th><th>Logo</th><th>Actions</th>
  </tr></thead><tbody>${rows.map(site => `
    <tr data-id="${site.id}">
      <td>${site.id}</td>
      <td>${site.siteKey}</td>
      <td>${site.siteName}</td>
      <td class="cut-cell">${site.baseUrl}</td>
      <td><input type="checkbox" data-toggle="active" ${site.active ? "checked" : ""}></td>
      <td><input type="checkbox" data-toggle="couponEnabled" ${site.couponEnabled ? "checked" : ""}></td>
      <td><input type="checkbox" data-toggle="brandEnabled" ${site.brandEnabled ? "checked" : ""}></td>
      <td><input type="checkbox" data-toggle="logoEnabled" ${site.logoEnabled ? "checked" : ""}></td>
      <td><button class="admin-mini-btn" data-edit-site="${site.id}">Edit</button> <button class="admin-mini-btn" data-del-site="${site.id}">Delete</button></td>
    </tr>
  `).join("")}</tbody>`;
}

async function loadCrawlerSites() {
  cachedCrawlerSites = await (await adminFetch("/api/admin/crawler/sites")).json();
  renderCrawlerSites(cachedCrawlerSites);
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
    crawlerStatus.textContent = "Crawler site save failed";
    return;
  }
  clearCrawlerSiteForm();
  await loadCrawlerSites();
  crawlerStatus.textContent = "Crawler site saved";
}

async function loadContent() {
  const data = await (await adminFetch("/api/admin/content")).json();
  contentThemePreset.value = data.themePreset || "scheme-a";
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
  contentStatus.textContent = "Saving...";
  const body = {
    themePreset: contentThemePreset.value || "scheme-a",
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
  contentStatus.textContent = response.ok ? "Saved" : "Save failed";
}

async function uploadHeroImage() {
  if (!heroImageFile.files.length) {
    contentStatus.textContent = "Choose a hero image first";
    return;
  }
  const formData = new FormData();
  formData.append("file", heroImageFile.files[0]);
  const response = await adminFetch("/api/admin/uploads/images", { method: "POST", body: formData });
  if (!response.ok) {
    contentStatus.textContent = "Upload failed";
    return;
  }
  const data = await response.json();
  contentHeroBgImageUrl.value = data.url;
  contentStatus.textContent = `Uploaded: ${data.url}`;
}

async function saveCrawler() {
  crawlerStatus.textContent = "Saving...";
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
  crawlerStatus.textContent = response.ok ? "Saved" : "Save failed";
  await loadCrawler();
}

async function runCrawler(endpoint, title) {
  crawlerStatus.textContent = `Running ${title}...`;
  const response = await adminFetch(endpoint, { method: "POST" });
  crawlerStatus.textContent = await response.text();
  await Promise.all([loadCrawler(), loadCoupons(), loadBrands()]);
}

function clearCouponForm() {
  saveCouponBtn.dataset.editId = "";
  saveCouponBtn.textContent = "Add Coupon";
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
  const state = sortState.coupons;
  const rows = sortedCopy(coupons, state, {
    id: (a, b) => Number(a.id || 0) - Number(b.id || 0),
    clickCount: (a, b) => Number(a.clickCount || 0) - Number(b.clickCount || 0),
    createdAt: (a, b) => toTimestamp(a.createdAt) - toTimestamp(b.createdAt),
    updatedAt: (a, b) => toTimestamp(a.updatedAt) - toTimestamp(b.updatedAt)
  });
  couponTable.innerHTML = `<thead><tr>
    <th data-sort="id">${sortLabel("ID", state, "id")}</th>
    <th data-sort="store">${sortLabel("Store", state, "store")}</th>
    <th data-sort="title">${sortLabel("Title", state, "title")}</th>
    <th data-sort="category">${sortLabel("Category", state, "category")}</th>
    <th data-sort="expires">${sortLabel("Expires", state, "expires")}</th>
    <th data-sort="couponCode">${sortLabel("Code", state, "couponCode")}</th>
    <th data-sort="clickCount">${sortLabel("Clicks", state, "clickCount")}</th>
    <th data-sort="createdAt">${sortLabel("Created", state, "createdAt")}</th>
    <th data-sort="updatedAt">${sortLabel("Updated", state, "updatedAt")}</th>
    <th>Affiliate URL</th><th>Logo</th><th>Actions</th>
  </tr></thead><tbody>${rows.map(c => `
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
      <td><button class='admin-mini-btn' data-save-coupon='${c.id}'>Save</button> <button class='admin-mini-btn' data-del-coupon='${c.id}'>Delete</button></td>
    </tr>`).join("")}</tbody>`;
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
  saveBrandBtn.textContent = "Add Brand";
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
  const state = sortState.brands;
  const rows = sortedCopy(brands, state, {
    id: (a, b) => Number(a.id || 0) - Number(b.id || 0),
    createdAt: (a, b) => toTimestamp(a.createdAt) - toTimestamp(b.createdAt),
    updatedAt: (a, b) => toTimestamp(a.updatedAt) - toTimestamp(b.updatedAt)
  });
  brandTable.innerHTML = `<thead><tr>
    <th data-sort="id">${sortLabel("ID", state, "id")}</th>
    <th data-sort="storeName">${sortLabel("Store", state, "storeName")}</th>
    <th data-sort="slug">${sortLabel("Slug", state, "slug")}</th>
    <th data-sort="title">${sortLabel("Title", state, "title")}</th>
    <th data-sort="createdAt">${sortLabel("Created", state, "createdAt")}</th>
    <th data-sort="updatedAt">${sortLabel("Updated", state, "updatedAt")}</th>
    <th>Summary</th><th>Official URL</th><th>Affiliate URL</th><th>Logo</th><th>Hero Image</th><th>Description</th><th>Actions</th>
  </tr></thead><tbody>${rows.map(b => `
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
      <td><button class='admin-mini-btn' data-save-brand='${b.id}'>Save</button> <button class='admin-mini-btn' data-del-brand='${b.id}'>Delete</button></td>
    </tr>`).join("")}</tbody>`;
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
  saveBlogBtn.textContent = "Add Blog";
  [blogTitle, blogSummary, blogCover, blogContent].forEach(el => el.value = "");
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
  const state = sortState.blogs;
  const rows = sortedCopy(blogs, state, {
    id: (a, b) => Number(a.id || 0) - Number(b.id || 0),
    createdAt: (a, b) => toTimestamp(a.createdAt) - toTimestamp(b.createdAt),
    updatedAt: (a, b) => toTimestamp(a.updatedAt) - toTimestamp(b.updatedAt)
  });
  blogTable.innerHTML = `<thead><tr>
    <th data-sort="id">${sortLabel("ID", state, "id")}</th>
    <th data-sort="title">${sortLabel("Title", state, "title")}</th>
    <th data-sort="createdAt">${sortLabel("Created", state, "createdAt")}</th>
    <th data-sort="updatedAt">${sortLabel("Updated", state, "updatedAt")}</th>
    <th>Summary</th><th>Content</th><th>Cover</th><th>Published</th><th>Actions</th>
  </tr></thead><tbody>${rows.map(b => `
    <tr data-id='${b.id}'>
      <td>${b.id}</td>
      <td class='editable-cell' data-field='title'>${b.title || ""}</td>
      <td>${formatDateTime(b.createdAt)}</td>
      <td>${formatDateTime(b.updatedAt)}</td>
      <td class='editable-cell' data-field='summary'>${b.summary || ""}</td>
      <td class='editable-cell cut-cell' data-field='content'>${b.content || ""}</td>
      <td class='editable-cell cut-cell' data-field='coverImageUrl'>${b.coverImageUrl || ""}</td>
      <td class='editable-cell' data-field='published'>${b.published}</td>
      <td><button class='admin-mini-btn' data-save-blog='${b.id}'>Save</button> <button class='admin-mini-btn' data-del-blog='${b.id}'>Delete</button></td>
    </tr>`).join("")}</tbody>`;
}

async function loadBlogs() {
  cachedBlogs = await (await adminFetch("/api/admin/blogs")).json();
  renderBlogRows(cachedBlogs);
  statBlogs.textContent = String(cachedBlogs.filter(x => x.published).length);
}

async function saveBlog() {
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

async function uploadBlogImage() {
  if (!blogImageFile.files.length) {
    blogStatus.textContent = "Choose an image file first";
    return;
  }
  const formData = new FormData();
  formData.append("file", blogImageFile.files[0]);
  const response = await adminFetch("/api/admin/uploads/images", { method: "POST", body: formData });
  if (!response.ok) {
    blogStatus.textContent = "Upload failed";
    return;
  }
  const data = await response.json();
  blogCover.value = data.url;
  blogStatus.textContent = `Uploaded: ${data.url}`;
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
  adsStatus.textContent = "Saving...";
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
  adsStatus.textContent = response.ok ? "Saved" : "Save failed";
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
    saveCrawlerSiteBtn.textContent = "Update Crawler Site";
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
runCouponCrawlerBtn.addEventListener("click", () => runCrawler("/api/admin/crawler/run-coupons", "coupon crawler"));
runBrandCrawlerBtn.addEventListener("click", () => runCrawler("/api/admin/crawler/run-brands", "brand crawler"));
runBrandLogoCrawlerBtn.addEventListener("click", () => runCrawler("/api/admin/crawler/run-brand-logos", "brand logo crawler"));
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
logoutBtn.addEventListener("click", event => { event.preventDefault(); logout(); });
if (logLang) {
  logLang.addEventListener("change", renderCrawlerLogs);
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
  const ok = await checkAuth();
  if (!ok) return;
  await Promise.all([loadCrawler(), loadContent(), loadCoupons(), loadBrands(), loadBlogs(), loadAds()]);
  clearCrawlerSiteForm();
})();




