const crawlerEnabled = document.getElementById("crawlerEnabled");
const saveCrawlerBtn = document.getElementById("saveCrawlerBtn");
const runCrawlerBtn = document.getElementById("runCrawlerBtn");
const crawlerStatus = document.getElementById("crawlerStatus");
const logPanel = document.getElementById("logPanel");
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
const saveContentBtn = document.getElementById("saveContentBtn");
const contentStatus = document.getElementById("contentStatus");

const couponStore = document.getElementById("couponStore");
const couponTitle = document.getElementById("couponTitle");
const couponCategory = document.getElementById("couponCategory");
const couponExpires = document.getElementById("couponExpires");
const couponCode = document.getElementById("couponCode");
const couponAffiliate = document.getElementById("couponAffiliate");
const couponLogo = document.getElementById("couponLogo");
const couponSource = document.getElementById("couponSource");
const saveCouponBtn = document.getElementById("saveCouponBtn");
const clearCouponBtn = document.getElementById("clearCouponBtn");

const brandStoreName = document.getElementById("brandStoreName");
const brandSlug = document.getElementById("brandSlug");
const brandTitle = document.getElementById("brandTitle");
const brandSummary = document.getElementById("brandSummary");
const brandOfficialUrl = document.getElementById("brandOfficialUrl");
const brandLogoUrl = document.getElementById("brandLogoUrl");
const brandHeroImageUrl = document.getElementById("brandHeroImageUrl");
const brandDescription = document.getElementById("brandDescription");
const saveBrandBtn = document.getElementById("saveBrandBtn");
const clearBrandBtn = document.getElementById("clearBrandBtn");

const blogTitle = document.getElementById("blogTitle");
const blogSummary = document.getElementById("blogSummary");
const blogCover = document.getElementById("blogCover");
const blogPublished = document.getElementById("blogPublished");
const blogContent = document.getElementById("blogContent");
const saveBlogBtn = document.getElementById("saveBlogBtn");
const clearBlogBtn = document.getElementById("clearBlogBtn");
const blogImageFile = document.getElementById("blogImageFile");
const uploadImageBtn = document.getElementById("uploadImageBtn");
const blogStatus = document.getElementById("blogStatus");

const adsStripEnabled = document.getElementById("adsStripEnabled");
const adsStripText = document.getElementById("adsStripText");
const adsStripLink = document.getElementById("adsStripLink");
const adsHomeTopEnabled = document.getElementById("adsHomeTopEnabled");
const adsHomeMidEnabled = document.getElementById("adsHomeMidEnabled");
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

async function loadCrawler() {
  const settings = await (await adminFetch("/api/admin/settings")).json();
  crawlerEnabled.checked = settings.crawlerEnabled;
  const logs = await (await adminFetch("/api/admin/logs")).json();
  logPanel.textContent = (logs || []).map(log => `[${log.createdAt}] [${log.level}] ${log.message}`).join("\n") || "No logs yet";
  statCrawler.textContent = settings.crawlerEnabled ? "Enabled" : "Disabled";
}

async function loadContent() {
  const data = await (await adminFetch("/api/admin/content")).json();
  contentHeroEyebrow.value = data.heroEyebrow || "";
  contentHeroTitle.value = data.heroTitle || "";
  contentHeroSubtitle.value = data.heroSubtitle || "";
}

async function saveContent() {
  contentStatus.textContent = "Saving...";
  const body = {
    heroEyebrow: contentHeroEyebrow.value,
    heroTitle: contentHeroTitle.value,
    heroSubtitle: contentHeroSubtitle.value
  };
  const response = await adminFetch("/api/admin/content", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  contentStatus.textContent = response.ok ? "Saved" : "Save failed";
}

async function saveCrawler() {
  crawlerStatus.textContent = "Saving...";
  const response = await adminFetch("/api/admin/settings", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ crawlerEnabled: crawlerEnabled.checked })
  });
  crawlerStatus.textContent = response.ok ? "Saved" : "Save failed";
  await loadCrawler();
}

async function runCrawler() {
  crawlerStatus.textContent = "Running...";
  const response = await adminFetch("/api/admin/crawler/run", { method: "POST" });
  crawlerStatus.textContent = await response.text();
  await Promise.all([loadCrawler(), loadCoupons()]);
}

function clearCouponForm() {
  saveCouponBtn.dataset.editId = "";
  saveCouponBtn.textContent = "Add Coupon";
  [couponStore, couponTitle, couponCategory, couponExpires, couponCode, couponAffiliate, couponLogo, couponSource].forEach(el => el.value = "");
}

function renderCouponRows(coupons) {
  couponTable.innerHTML = `<thead><tr><th>ID</th><th>Store</th><th>Title</th><th>Code</th><th>Affiliate URL</th><th>Actions</th></tr></thead><tbody>${coupons.map(c => `
    <tr><td>${c.id}</td><td>${c.store}</td><td>${c.title}</td><td>${c.couponCode}</td><td class="cut-cell">${c.affiliateUrl}</td>
    <td><button class="admin-mini-btn" data-edit-coupon="${c.id}">Edit</button> <button class="admin-mini-btn" data-del-coupon="${c.id}">Delete</button></td></tr>`).join("")}</tbody>`;

  couponTable.querySelectorAll("[data-del-coupon]").forEach(btn => btn.addEventListener("click", async () => {
    await adminFetch(`/api/admin/coupons?id=${btn.dataset.delCoupon}`, { method: "DELETE" });
    await loadCoupons();
    clearCouponForm();
  }));

  couponTable.querySelectorAll("[data-edit-coupon]").forEach(btn => btn.addEventListener("click", () => {
    const item = cachedCoupons.find(x => x.id === Number(btn.dataset.editCoupon));
    if (!item) return;
    couponStore.value = item.store;
    couponTitle.value = item.title;
    couponCategory.value = item.category;
    couponExpires.value = item.expires;
    couponCode.value = item.couponCode;
    couponAffiliate.value = item.affiliateUrl;
    couponLogo.value = item.logoUrl;
    couponSource.value = item.source;
    saveCouponBtn.dataset.editId = String(item.id);
    saveCouponBtn.textContent = "Update Coupon";
    showTab("coupons");
  }));
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
    logoUrl: couponLogo.value,
    source: couponSource.value || "admin"
  };
  await adminFetch("/api/admin/coupons", { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
  clearCouponForm();
  await loadCoupons();
}

function clearBrandForm() {
  saveBrandBtn.dataset.editId = "";
  saveBrandBtn.textContent = "Add Brand";
  [brandStoreName, brandSlug, brandTitle, brandSummary, brandOfficialUrl, brandLogoUrl, brandHeroImageUrl, brandDescription].forEach(el => el.value = "");
}

function renderBrandRows(brands) {
  brandTable.innerHTML = `<thead><tr><th>ID</th><th>Store</th><th>Slug</th><th>Official URL</th><th>Actions</th></tr></thead><tbody>${brands.map(b => `
    <tr><td>${b.id}</td><td>${b.storeName}</td><td>${b.slug}</td><td class="cut-cell">${b.officialUrl}</td>
    <td><button class="admin-mini-btn" data-edit-brand="${b.id}">Edit</button> <button class="admin-mini-btn" data-del-brand="${b.id}">Delete</button></td></tr>`).join("")}</tbody>`;

  brandTable.querySelectorAll("[data-del-brand]").forEach(btn => btn.addEventListener("click", async () => {
    await adminFetch(`/api/admin/brands?id=${btn.dataset.delBrand}`, { method: "DELETE" });
    await loadBrands();
    clearBrandForm();
  }));

  brandTable.querySelectorAll("[data-edit-brand]").forEach(btn => btn.addEventListener("click", () => {
    const item = cachedBrands.find(x => x.id === Number(btn.dataset.editBrand));
    if (!item) return;
    brandStoreName.value = item.storeName;
    brandSlug.value = item.slug;
    brandTitle.value = item.title;
    brandSummary.value = item.summary;
    brandOfficialUrl.value = item.officialUrl;
    brandLogoUrl.value = item.logoUrl;
    brandHeroImageUrl.value = item.heroImageUrl;
    brandDescription.value = item.description;
    saveBrandBtn.dataset.editId = String(item.id);
    saveBrandBtn.textContent = "Update Brand";
    showTab("brands");
  }));
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
    officialUrl: brandOfficialUrl.value
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

function renderBlogRows(blogs) {
  blogTable.innerHTML = `<thead><tr><th>ID</th><th>Title</th><th>Published</th><th>Cover</th><th>Actions</th></tr></thead><tbody>${blogs.map(b => `
    <tr><td>${b.id}</td><td>${b.title}</td><td>${b.published}</td><td class="cut-cell">${b.coverImageUrl}</td>
    <td><button class="admin-mini-btn" data-edit-blog="${b.id}">Edit</button> <button class="admin-mini-btn" data-del-blog="${b.id}">Delete</button></td></tr>`).join("")}</tbody>`;

  blogTable.querySelectorAll("[data-del-blog]").forEach(btn => btn.addEventListener("click", async () => {
    await adminFetch(`/api/admin/blogs?id=${btn.dataset.delBlog}`, { method: "DELETE" });
    await loadBlogs();
    clearBlogForm();
  }));

  blogTable.querySelectorAll("[data-edit-blog]").forEach(btn => btn.addEventListener("click", () => {
    const item = cachedBlogs.find(x => x.id === Number(btn.dataset.editBlog));
    if (!item) return;
    blogTitle.value = item.title;
    blogSummary.value = item.summary;
    blogCover.value = item.coverImageUrl;
    blogContent.value = item.content;
    blogPublished.value = String(item.published);
    saveBlogBtn.dataset.editId = String(item.id);
    saveBlogBtn.textContent = "Update Blog";
    showTab("blogs");
  }));
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

saveCrawlerBtn.addEventListener("click", saveCrawler);
runCrawlerBtn.addEventListener("click", runCrawler);
saveContentBtn.addEventListener("click", saveContent);
saveCouponBtn.addEventListener("click", saveCoupon);
clearCouponBtn.addEventListener("click", clearCouponForm);
saveBrandBtn.addEventListener("click", saveBrand);
clearBrandBtn.addEventListener("click", clearBrandForm);
saveBlogBtn.addEventListener("click", saveBlog);
clearBlogBtn.addEventListener("click", clearBlogForm);
uploadImageBtn.addEventListener("click", uploadBlogImage);
saveAdsBtn.addEventListener("click", saveAds);
logoutBtn.addEventListener("click", event => { event.preventDefault(); logout(); });

(async function init() {
  const ok = await checkAuth();
  if (!ok) return;
  await Promise.all([loadCrawler(), loadContent(), loadCoupons(), loadBrands(), loadBlogs(), loadAds()]);
})();
