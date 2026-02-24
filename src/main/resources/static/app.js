const couponList = document.getElementById("couponList");
const couponTemplate = document.getElementById("couponCardTemplate");
const storeGrid = document.getElementById("storeGrid");
const storeTemplate = document.getElementById("storeTileTemplate");
const blogList = document.getElementById("blogList");
const blogTemplate = document.getElementById("blogCardTemplate");
const categoryGrid = document.getElementById("categoryGrid");

const searchInput = document.getElementById("searchInput");
const searchForm = document.getElementById("searchForm");
const filterChips = document.getElementById("filterChips");

const heroSection = document.getElementById("heroSection");
const heroEyebrow = document.getElementById("heroEyebrow");
const heroTitle = document.getElementById("heroTitle");
const heroSubtitle = document.getElementById("heroSubtitle");
const heroTicketLabel = document.getElementById("heroTicketLabel");
const heroTicketCount = document.getElementById("heroTicketCount");
const heroTicketMeta = document.getElementById("heroTicketMeta");
const adStrip = document.getElementById("adStrip");
const adStripLink = document.getElementById("adStripLink");
const adStripText = document.getElementById("adStripText");
const dealsLayout = document.getElementById("dealsLayout");
const homeTopAdWrap = document.getElementById("homeTopAdWrap");
const homeTopAd = document.getElementById("homeTopAd");
const homeMidAdWrap = document.getElementById("homeMidAdWrap");
const homeMidAd = document.getElementById("homeMidAd");
const homeBottomAdWrap = document.getElementById("homeBottomAdWrap");
const homeBottomAd = document.getElementById("homeBottomAd");
const homeSideLeftAdWrap = document.getElementById("homeSideLeftAdWrap");
const homeSideLeftAd = document.getElementById("homeSideLeftAd");
const homeSideRightAdWrap = document.getElementById("homeSideRightAdWrap");
const homeSideRightAd = document.getElementById("homeSideRightAd");
const blogTopAdWrap = document.getElementById("blogTopAdWrap");
const blogTopAd = document.getElementById("blogTopAd");
const blogInlineAdWrap = document.getElementById("blogInlineAdWrap");
const blogInlineAd = document.getElementById("blogInlineAd");
const blogBottomAdWrap = document.getElementById("blogBottomAdWrap");
const blogBottomAd = document.getElementById("blogBottomAd");
const homeBrandLogo = document.querySelector(".home-brand-logo");
const homeBrandTitle = document.querySelector(".home-brand strong");
const homeBrandSlogan = document.querySelector(".home-brand small");

const footerTagline = document.getElementById("footerTagline");
const footerTwitterLink = document.getElementById("footerTwitterLink");
const footerInstagramLink = document.getElementById("footerInstagramLink");
const footerFacebookLink = document.getElementById("footerFacebookLink");
const footerYoutubeLink = document.getElementById("footerYoutubeLink");
const footerAboutLink = document.getElementById("footerAboutLink");
const footerPrivacyLink = document.getElementById("footerPrivacyLink");
const footerContactLink = document.getElementById("footerContactLink");
const footerSubmitCouponLink = document.getElementById("footerSubmitCouponLink");
const footerAffiliateDisclosureLink = document.getElementById("footerAffiliateDisclosureLink");

let activeFilter = "all";
let searchTerm = "";
let allCouponsCache = [];
let displayedCouponCount = 0;
const couponPageSize = 10;

const categoryIcons = {
  electronics: "bi-phone",
  fashion: "bi-bag",
  travel: "bi-airplane",
  beauty: "bi-stars",
  home: "bi-house-door",
  food: "bi-cup-hot"
};

function normalizeColor(value) {
  if (!value) return "#F8FAFC";
  const color = value.trim();
  return /^#[0-9a-fA-F]{6}$/.test(color) ? color : "#F8FAFC";
}

function normalizeTheme(theme) {
  const value = (theme || "").toLowerCase();
  if (value === "scheme-b" || value === "scheme-c") return value;
  return "scheme-a";
}

function toSafeText(value, fallback = "") {
  if (value == null || value === "") return fallback;
  return String(value);
}

function extractDiscount(title) {
  const match = toSafeText(title).match(/(\d{1,2})\s*%/);
  if (match) return Number(match[1]);
  return null;
}

function humanTimer(expires) {
  const text = toSafeText(expires, "Ends soon");
  const lower = text.toLowerCase();
  if (lower.includes("today") || lower.includes("tonight")) return "Expires today";
  const dayMatch = lower.match(/(\d+)\s*day/);
  if (dayMatch) return `Expires in ${dayMatch[1]} days`;
  return "Limited time";
}

function isHotCoupon(coupon) {
  const clicks = Number(coupon.clickCount || 0);
  if (clicks >= 10) return true;
  const title = toSafeText(coupon.title).toLowerCase();
  return title.includes("hot") || title.includes("flash") || title.includes("limited");
}

async function fetchCoupons() {
  const params = new URLSearchParams({ category: activeFilter, q: searchTerm });
  const response = await fetch(`/api/coupons?${params.toString()}`);
  if (!response.ok) throw new Error("Failed to fetch coupons");
  return response.json();
}

async function fetchAllCouponsForStats() {
  const params = new URLSearchParams({ category: "all", q: "" });
  const response = await fetch(`/api/coupons?${params.toString()}`);
  if (!response.ok) throw new Error("Failed to fetch coupon stats");
  return response.json();
}

async function fetchBlogs() {
  const response = await fetch("/api/blogs");
  if (!response.ok) throw new Error("Failed to fetch blogs");
  return response.json();
}

async function fetchContent() {
  const response = await fetch("/api/content/public");
  if (!response.ok) throw new Error("Failed to fetch content");
  return response.json();
}

async function fetchAds() {
  const response = await fetch("/api/ads/public");
  if (!response.ok) throw new Error("Failed to fetch ads");
  return response.json();
}

async function fetchSeo() {
  const response = await fetch("/api/seo/public");
  if (!response.ok) throw new Error("Failed to fetch seo");
  return response.json();
}

async function revealCoupon(id) {
  const response = await fetch(`/api/coupons/${id}/reveal`, { method: "POST" });
  if (!response.ok) throw new Error("Failed to reveal coupon");
  return response.json();
}

function openCodePageAndRedirectCurrent(data, coupon) {
  const codePage = `/coupon-code.html?store=${encodeURIComponent(coupon.store)}&title=${encodeURIComponent(coupon.title)}&code=${encodeURIComponent(data.couponCode)}`;
  window.open(codePage, "_blank", "noopener");
  if (data.affiliateUrl) {
    window.location.assign(data.affiliateUrl);
  } else {
    window.location.assign(codePage);
  }
}

function applyContent(content) {
  if (!content) return;

  document.body.dataset.theme = normalizeTheme(content.themePreset);

  heroEyebrow.textContent = content.heroEyebrow || heroEyebrow.textContent;
  heroTitle.textContent = content.heroTitle || heroTitle.textContent;
  heroSubtitle.textContent = content.heroSubtitle || heroSubtitle.textContent;

  const bgColor = normalizeColor(content.heroBgColor);
  if (content.heroBgImageUrl) {
    heroSection.style.background = `${bgColor} url('${content.heroBgImageUrl}') center / cover no-repeat`;
  } else {
    heroSection.style.background = bgColor;
  }

  footerTagline.textContent = content.footerTagline || footerTagline.textContent;
  footerTwitterLink.href = content.footerTwitterUrl || footerTwitterLink.href;
  footerInstagramLink.href = content.footerInstagramUrl || footerInstagramLink.href;
  footerFacebookLink.href = content.footerFacebookUrl || footerFacebookLink.href;
  footerYoutubeLink.href = content.footerYoutubeUrl || footerYoutubeLink.href;

  footerAboutLink.href = content.footerAboutUrl || footerAboutLink.href;
  footerPrivacyLink.href = content.footerPrivacyUrl || footerPrivacyLink.href;
  footerContactLink.href = content.footerContactUrl || footerContactLink.href;
  footerSubmitCouponLink.href = content.footerSubmitCouponUrl || footerSubmitCouponLink.href;
  footerAffiliateDisclosureLink.href = content.footerAffiliateDisclosureUrl || footerAffiliateDisclosureLink.href;
  if (homeBrandTitle) homeBrandTitle.textContent = content.siteName || homeBrandTitle.textContent;
  if (homeBrandSlogan) homeBrandSlogan.textContent = content.siteSlogan || homeBrandSlogan.textContent;
  if (homeBrandLogo) {
    if (content.siteLogoImageUrl) {
      homeBrandLogo.innerHTML = `<img src="${content.siteLogoImageUrl}" alt="logo" style="width:100%;height:100%;object-fit:contain;">`;
    } else {
      homeBrandLogo.textContent = content.siteLogoText || homeBrandLogo.textContent;
    }
  }
}

function applyAds(ads) {
  if (!adStrip || !ads) return;
  if (!ads.stripEnabled || !ads.stripText) {
    adStrip.classList.add("hidden");
    return;
  }
  adStrip.classList.remove("hidden");
  adStripText.textContent = `${ads.stripText} • ${ads.stripText}`;
  adStripLink.href = ads.stripLink || "#";

  const renderSlot = (wrap, box, enabled, label) => {
    if (!wrap || !box) return;
    if (!enabled) {
      wrap.classList.add("hidden");
      box.innerHTML = "";
      return;
    }
    wrap.classList.remove("hidden");
    const title = ads.stripText || "Ad Placement";
    const link = ads.stripLink || "#";
    box.innerHTML = `<a href="${link}" target="_blank" rel="noopener noreferrer" class="ad-box-fallback">${label}: ${title}</a>`;
  };

  renderSlot(homeTopAdWrap, homeTopAd, !!ads.homeTopEnabled, "Home Top Ad");
  renderSlot(homeMidAdWrap, homeMidAd, !!ads.homeMidEnabled, "Home Mid Ad");
  renderSlot(homeBottomAdWrap, homeBottomAd, !!ads.homeBottomEnabled, "Home Bottom Ad");
  renderSlot(homeSideLeftAdWrap, homeSideLeftAd, !!ads.homeSideLeftEnabled, "Home Middle Left Ad");
  renderSlot(homeSideRightAdWrap, homeSideRightAd, !!ads.homeSideRightEnabled, "Home Middle Right Ad");
  renderSlot(blogTopAdWrap, blogTopAd, !!ads.blogTopEnabled, "Blog Top Ad");
  renderSlot(blogInlineAdWrap, blogInlineAd, !!ads.blogInlineEnabled, "Blog Inline Ad");
  renderSlot(blogBottomAdWrap, blogBottomAd, !!ads.blogBottomEnabled, "Blog Bottom Ad");

  if (dealsLayout) {
    dealsLayout.classList.toggle("with-side-left", !!ads.homeSideLeftEnabled);
    dealsLayout.classList.toggle("with-side-right", !!ads.homeSideRightEnabled);
  }
}

function applySeo(seo) {
  if (!seo) return;
  if (seo.title) document.title = seo.title;
  const setMeta = (name, content, attr = "name") => {
    if (!content) return;
    let node = document.head.querySelector(`meta[${attr}="${name}"]`);
    if (!node) {
      node = document.createElement("meta");
      node.setAttribute(attr, name);
      document.head.appendChild(node);
    }
    node.setAttribute("content", content);
  };
  setMeta("description", seo.description);
  setMeta("keywords", seo.keywords);
  setMeta("og:title", seo.title, "property");
  setMeta("og:description", seo.description, "property");
  setMeta("og:image", seo.ogImageUrl, "property");
}

function renderCategoryGrid(coupons) {
  const categories = Array.from(new Set(coupons.map(coupon => toSafeText(coupon.category).toLowerCase()))).filter(Boolean);
  if (!categories.length) return;

  categoryGrid.innerHTML = categories.map(category => `
    <a class="home-category-tile" href="/categories.html">
      <i class="bi ${categoryIcons[category] || "bi-grid"}"></i>
      <span>${category}</span>
    </a>
  `).join("");
}

function renderFilterChips(coupons) {
  const categories = Array.from(new Set(coupons.map(coupon => toSafeText(coupon.category).toLowerCase()))).filter(Boolean);
  const all = ["all", ...categories];
  if (!all.includes("expired")) {
    all.push("expired");
  }

  filterChips.innerHTML = all.map(category => `
    <button class="home-chip ${category === activeFilter ? "active" : ""}" data-filter="${category}">${category}</button>
  `).join("");
}

function renderStores(coupons) {
  const map = new Map();
  coupons.forEach(coupon => {
    const key = toSafeText(coupon.store);
    if (!key) return;
    if (!map.has(key)) {
      map.set(key, { store: key, logoUrl: coupon.logoUrl, coupons: [], maxDiscount: null });
    }
    const entry = map.get(key);
    entry.coupons.push(coupon);
    const d = extractDiscount(coupon.title);
    if (d != null) {
      entry.maxDiscount = entry.maxDiscount == null ? d : Math.max(entry.maxDiscount, d);
    }
  });

  const stores = Array.from(map.values())
    .sort((a, b) => b.coupons.length - a.coupons.length || a.store.localeCompare(b.store))
    .slice(0, 12);

  storeGrid.innerHTML = "";
  stores.forEach(item => {
    const node = storeTemplate.content.cloneNode(true);
    const logo = node.querySelector(".store-logo");
    const link = node.querySelector(".store-link");
    node.querySelector(".store-name").textContent = item.store;
    node.querySelector(".store-discount").textContent = `Up to ${item.maxDiscount ?? Math.min(55, 10 + item.coupons.length * 5)}% Off`;
    node.querySelector(".store-count").textContent = `${item.coupons.length} active coupons`;

    logo.src = item.logoUrl || "/logos/default.svg";
    logo.alt = `${item.store} logo`;
    logo.addEventListener("error", () => { logo.src = "/logos/default.svg"; }, { once: true });

    link.href = `/brand.html?store=${encodeURIComponent(item.store)}`;
    storeGrid.appendChild(node);
  });
}

function sortDeals(coupons) {
  return [...coupons].sort((a, b) => {
    const hotDiff = Number(isHotCoupon(b)) - Number(isHotCoupon(a));
    if (hotDiff !== 0) return hotDiff;
    return Number(b.clickCount || 0) - Number(a.clickCount || 0);
  });
}

function buildCouponNode(coupon) {
  const node = couponTemplate.content.cloneNode(true);
  const logo = node.querySelector(".home-coupon-logo");
  node.querySelector(".coupon-store").textContent = toSafeText(coupon.store);
  node.querySelector(".coupon-title").textContent = toSafeText(coupon.title);
  const meta = node.querySelector(".coupon-meta");
  meta.textContent = `${toSafeText(coupon.category)} | ${toSafeText(coupon.expires)} | ${Number(coupon.clickCount || 0)} clicks`;
  node.querySelector(".badge-timer").textContent = humanTimer(coupon.expires);
  logo.src = coupon.logoUrl || "/logos/default.svg";
  logo.alt = `${coupon.store || "brand"} logo`;
  logo.addEventListener("error", () => { logo.src = "/logos/default.svg"; }, { once: true });
  if (coupon.expired) {
    node.querySelector(".home-coupon-card").classList.add("expired");
    meta.classList.add("expired");
  }

  const hotBadge = node.querySelector(".badge-hot");
  if (isHotCoupon(coupon)) {
    hotBadge.classList.remove("hidden");
    hotBadge.textContent = "Hot";
  }

  const btn = node.querySelector(".reveal-btn");
  btn.addEventListener("click", async () => {
    btn.disabled = true;
    btn.textContent = "Loading...";
    try {
      const data = await revealCoupon(coupon.id);
      btn.textContent = "Redirecting...";
      openCodePageAndRedirectCurrent(data, coupon);
    } catch (_) {
      btn.textContent = "Try again";
      btn.disabled = false;
    }
  });
  return node;
}

function renderMoreCoupons() {
  const existingWrap = document.getElementById("couponListLoadMoreWrap");
  if (existingWrap) {
    existingWrap.remove();
  }
  const next = allCouponsCache.slice(displayedCouponCount, displayedCouponCount + couponPageSize);
  next.forEach(coupon => couponList.appendChild(buildCouponNode(coupon)));
  displayedCouponCount += next.length;

  if (displayedCouponCount < allCouponsCache.length) {
    const wrap = document.createElement("div");
    wrap.id = "couponListLoadMoreWrap";
    wrap.className = "coupon-load-more-wrap";
    const button = document.createElement("button");
    button.type = "button";
    button.id = "couponListLoadMoreBtn";
    button.className = "home-btn home-btn-primary";
    button.textContent = "Load More Deals";
    button.addEventListener("click", renderMoreCoupons);
    wrap.appendChild(button);
    couponList.appendChild(wrap);
  }
}

function setupCouponLoadMoreButton() {
  renderMoreCoupons();
}

function renderCoupons(coupons) {
  couponList.innerHTML = "";
  if (!coupons.length) {
    couponList.innerHTML = `<article class="home-coupon-card"><p>No coupons match your search.</p></article>`;
    return;
  }
  const active = coupons.filter(c => !c.expired);
  const expired = coupons.filter(c => c.expired);
  allCouponsCache = [...sortDeals(active), ...sortDeals(expired)];
  displayedCouponCount = 0;
  setupCouponLoadMoreButton();
}

function renderBlogs(blogs) {
  blogList.innerHTML = "";
  if (!blogs.length) {
    blogList.innerHTML = `<article class="home-blog-card"><div class="blog-content"><h3>No guides yet</h3></div></article>`;
    return;
  }

  blogs.slice(0, 6).forEach(blog => {
    const node = blogTemplate.content.cloneNode(true);
    const cover = node.querySelector(".blog-cover");
    cover.src = blog.coverImageUrl || "/logos/default.svg";
    cover.alt = blog.title;
    cover.addEventListener("error", () => { cover.src = "/logos/default.svg"; }, { once: true });
    node.querySelector(".blog-title").textContent = toSafeText(blog.title);
    node.querySelector(".blog-summary").textContent = toSafeText(blog.summary);
    blogList.appendChild(node);
  });
}

function isTodayDateTime(value) {
  if (!value) return false;
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return false;
  const now = new Date();
  return d.getFullYear() === now.getFullYear()
    && d.getMonth() === now.getMonth()
    && d.getDate() === now.getDate();
}

function renderHeroTicketStats(coupons) {
  if (!heroTicketLabel || !heroTicketCount || !heroTicketMeta) return;
  const all = Array.isArray(coupons) ? coupons : [];
  const workingCodes = all.filter(coupon => !coupon.expired).length;
  const updatedToday = all.filter(coupon => isTodayDateTime(coupon.updatedAt || coupon.createdAt)).length;
  heroTicketLabel.textContent = "Verified Today";
  heroTicketCount.textContent = `${workingCodes} Working Codes`;
  heroTicketMeta.textContent = `${updatedToday} coupons updated today from live database.`;
}

async function refreshCoupons() {
  try {
    const coupons = await fetchCoupons();
    renderFilterChips(coupons);
    renderCoupons(coupons);
    renderStores(coupons);
    renderCategoryGrid(coupons);
  } catch (_) {
    couponList.innerHTML = `<article class="home-coupon-card"><p>Unable to load coupons right now.</p></article>`;
  }
}

async function refreshBlogs() {
  try {
    const blogs = await fetchBlogs();
    renderBlogs(blogs);
  } catch (_) {
    blogList.innerHTML = `<article class="home-blog-card"><div class="blog-content"><h3>Unable to load guides right now.</h3></div></article>`;
  }
}

async function refreshHeroStats() {
  try {
    const coupons = await fetchAllCouponsForStats();
    renderHeroTicketStats(coupons);
  } catch (_) {
    if (heroTicketLabel) heroTicketLabel.textContent = "Live Data";
    if (heroTicketCount) heroTicketCount.textContent = "Working Codes Unavailable";
    if (heroTicketMeta) heroTicketMeta.textContent = "Unable to load live coupon stats right now.";
  }
}

filterChips.addEventListener("click", event => {
  const chip = event.target.closest("button[data-filter]");
  if (!chip) return;
  activeFilter = chip.dataset.filter;
  refreshCoupons();
});

searchForm.addEventListener("submit", event => {
  event.preventDefault();
  searchTerm = searchInput.value.trim().toLowerCase();
  refreshCoupons();
});

(async function init() {
  try {
    const [content, ads, seo] = await Promise.all([fetchContent(), fetchAds(), fetchSeo()]);
    applyContent(content);
    applyAds(ads);
    applySeo(seo);
  } catch (_) {
    document.body.dataset.theme = "scheme-a";
  }
  await Promise.all([refreshCoupons(), refreshBlogs(), refreshHeroStats()]);
})();
