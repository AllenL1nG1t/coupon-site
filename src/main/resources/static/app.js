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

const couponModal = document.getElementById("couponModal");
const modalStoreName = document.getElementById("modalStoreName");
const modalCouponCode = document.getElementById("modalCouponCode");
const modalCouponTitle = document.getElementById("modalCouponTitle");
const modalGoStoreBtn = document.getElementById("modalGoStoreBtn");
const modalCloseBtn = document.getElementById("modalCloseBtn");
const copyToast = document.getElementById("copyToast");

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

async function copyCode(code) {
  if (!code) return;
  try {
    await navigator.clipboard.writeText(code);
    showToast("Coupon copied");
  } catch (_) {
    showToast("Copy failed, please copy manually");
  }
}

function showToast(message) {
  copyToast.textContent = message;
  copyToast.classList.add("show");
  setTimeout(() => copyToast.classList.remove("show"), 1800);
}

function openModal(store, title, code, affiliateUrl) {
  modalStoreName.textContent = store;
  modalCouponTitle.textContent = title;
  modalCouponCode.textContent = code || "NO-CODE";
  modalGoStoreBtn.href = affiliateUrl || "#";
  couponModal.classList.remove("hidden");
}

function closeModal() {
  couponModal.classList.add("hidden");
}

async function fetchCoupons() {
  const params = new URLSearchParams({ category: activeFilter, q: searchTerm });
  const response = await fetch(`/api/coupons?${params.toString()}`);
  if (!response.ok) throw new Error("Failed to fetch coupons");
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

async function revealCoupon(id) {
  const response = await fetch(`/api/coupons/${id}/reveal`, { method: "POST" });
  if (!response.ok) throw new Error("Failed to reveal coupon");
  return response.json();
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
  node.querySelector(".coupon-store").textContent = toSafeText(coupon.store);
  node.querySelector(".coupon-title").textContent = toSafeText(coupon.title);
  node.querySelector(".coupon-meta").textContent = `${toSafeText(coupon.category)} | ${toSafeText(coupon.expires)} | ${Number(coupon.clickCount || 0)} clicks`;
  node.querySelector(".badge-timer").textContent = humanTimer(coupon.expires);

  const hotBadge = node.querySelector(".badge-hot");
  if (isHotCoupon(coupon)) {
    hotBadge.classList.remove("hidden");
    hotBadge.textContent = "Hot";
  }

  const btn = node.querySelector(".reveal-btn");
  btn.addEventListener("click", async () => {
    btn.disabled = true;
    btn.textContent = "Checking...";
    try {
      const data = await revealCoupon(coupon.id);
      await copyCode(data.couponCode);
      openModal(coupon.store, coupon.title, data.couponCode, data.affiliateUrl);
      if (data.affiliateUrl) {
        window.open(data.affiliateUrl, "_blank", "noopener");
      }
      btn.textContent = "Copied";
    } catch (_) {
      btn.textContent = "Try Again";
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
  allCouponsCache = sortDeals(coupons);
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

modalCloseBtn.addEventListener("click", closeModal);
couponModal.addEventListener("click", event => {
  if (event.target === couponModal) {
    closeModal();
  }
});

(async function init() {
  try {
    const content = await fetchContent();
    applyContent(content);
  } catch (_) {
    document.body.dataset.theme = "scheme-a";
  }
  await Promise.all([refreshCoupons(), refreshBlogs()]);
})();
