const couponList = document.getElementById("couponList");
const couponTemplate = document.getElementById("couponCardTemplate");
const storeGrid = document.getElementById("storeGrid");
const storeTemplate = document.getElementById("storeTileTemplate");
const blogList = document.getElementById("blogList");
const blogTemplate = document.getElementById("blogCardTemplate");
const searchInput = document.getElementById("searchInput");
const searchForm = document.getElementById("searchForm");
const filterChips = document.getElementById("filterChips");
const heroEyebrow = document.getElementById("heroEyebrow");
const heroTitle = document.getElementById("heroTitle");
const heroSubtitle = document.getElementById("heroSubtitle");

const adStrip = document.getElementById("adStrip");
const adStripLink = document.getElementById("adStripLink");
const adStripText = document.getElementById("adStripText");

const homeAdTop = document.getElementById("homeAdTop");
const homeAdMid = document.getElementById("homeAdMid");
const homeAdBottom = document.getElementById("homeAdBottom");
const blogAdTop = document.getElementById("blogAdTop");
const blogAdBottom = document.getElementById("blogAdBottom");

let activeFilter = "all";
let searchTerm = "";
let adSettings = null;
let adsenseLoaded = false;

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

async function fetchAds() {
  const response = await fetch("/api/ads/public");
  if (!response.ok) throw new Error("Failed to fetch ads");
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

function renderStores(coupons) {
  const stores = [];
  const seen = new Set();

  coupons.forEach(coupon => {
    if (seen.has(coupon.store)) return;
    seen.add(coupon.store);
    stores.push(coupon);
  });

  storeGrid.innerHTML = "";
  stores.slice(0, 8).forEach(store => {
    const node = storeTemplate.content.cloneNode(true);
    const link = node.querySelector(".store-link");
    const logo = node.querySelector(".store-logo");
    link.href = `/brand.html?store=${encodeURIComponent(store.store)}`;
    logo.src = store.logoUrl;
    logo.alt = `${store.store} logo`;
    logo.addEventListener("error", () => { logo.src = "/logos/default.svg"; }, { once: true });
    node.querySelector(".store-name").textContent = store.store;
    storeGrid.appendChild(node);
  });
}

function ensureAdsenseScript(clientId) {
  if (!clientId || adsenseLoaded) return;
  const script = document.createElement("script");
  script.async = true;
  script.src = `https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=${encodeURIComponent(clientId)}`;
  script.crossOrigin = "anonymous";
  document.head.appendChild(script);
  adsenseLoaded = true;
}

function renderAdsenseBlock(container, clientId, slotId) {
  const block = document.createElement("div");
  block.className = "ad-box";
  block.innerHTML = `
    <ins class="adsbygoogle"
      style="display:block"
      data-ad-client="${clientId}"
      data-ad-slot="${slotId}"
      data-ad-format="auto"
      data-full-width-responsive="true"></ins>
  `;
  container.appendChild(block);
  try {
    (window.adsbygoogle = window.adsbygoogle || []).push({});
  } catch (_) {
    // ignore runtime adsense push errors
  }
}

function applyHeroContent(content) {
  if (!content) return;
  heroEyebrow.textContent = content.heroEyebrow || heroEyebrow.textContent;
  heroTitle.textContent = content.heroTitle || heroTitle.textContent;
  heroSubtitle.textContent = content.heroSubtitle || heroSubtitle.textContent;
}

function renderFallbackAdBlock(container, label) {
  const block = document.createElement("div");
  block.className = "ad-box ad-box-fallback";
  block.textContent = label;
  container.appendChild(block);
}

function configureStrip(settings) {
  if (!settings.stripEnabled || !settings.stripText || !settings.stripLink) {
    adStrip.classList.add("hidden");
    return;
  }

  const repeated = Array(8).fill(settings.stripText).join("    •    ");
  adStripText.textContent = repeated;
  adStripLink.href = settings.stripLink;
  adStrip.classList.remove("hidden");
}

function renderPlacement(container, enabled, adsenseClientId, slotId, fallbackLabel) {
  container.innerHTML = "";
  container.classList.toggle("hidden", !enabled);
  if (!enabled) return;

  if (adsenseClientId && slotId) {
    ensureAdsenseScript(adsenseClientId);
    renderAdsenseBlock(container, adsenseClientId, slotId);
  } else {
    renderFallbackAdBlock(container, fallbackLabel);
  }
}

function applyHomeAds() {
  if (!adSettings) return;
  renderPlacement(homeAdTop, adSettings.homeTopEnabled, adSettings.adsenseClientId, adSettings.homeAdsenseSlot, "Home Top Ad");
  renderPlacement(homeAdMid, adSettings.homeMidEnabled, adSettings.adsenseClientId, adSettings.homeAdsenseSlot, "Home Mid Ad");
  renderPlacement(homeAdBottom, adSettings.homeBottomEnabled, adSettings.adsenseClientId, adSettings.homeAdsenseSlot, "Home Bottom Ad");
  renderPlacement(blogAdTop, adSettings.blogTopEnabled, adSettings.adsenseClientId, adSettings.blogAdsenseSlot, "Blog Top Ad");
  renderPlacement(blogAdBottom, adSettings.blogBottomEnabled, adSettings.adsenseClientId, adSettings.blogAdsenseSlot, "Blog Bottom Ad");
  configureStrip(adSettings);
}

function renderBlogs(blogs) {
  blogList.innerHTML = "";
  if (!blogs.length) {
    blogList.innerHTML = `<article class="blog-card"><div class="blog-content"><h3>No blog posts yet</h3></div></article>`;
    return;
  }

  blogs.slice(0, 6).forEach((blog, index) => {
    const node = blogTemplate.content.cloneNode(true);
    const cover = node.querySelector(".blog-cover");
    cover.src = blog.coverImageUrl || "/logos/default.svg";
    cover.alt = blog.title;
    cover.addEventListener("error", () => { cover.src = "/logos/default.svg"; }, { once: true });
    node.querySelector(".blog-title").textContent = blog.title;
    node.querySelector(".blog-summary").textContent = blog.summary;
    node.querySelector(".blog-body").textContent = blog.content;
    blogList.appendChild(node);

    if (adSettings?.blogInlineEnabled && index === 1) {
      const inlineAd = document.createElement("section");
      inlineAd.className = "ad-section inline-blog-ad";
      if (adSettings.adsenseClientId && adSettings.blogAdsenseSlot) {
        ensureAdsenseScript(adSettings.adsenseClientId);
        renderAdsenseBlock(inlineAd, adSettings.adsenseClientId, adSettings.blogAdsenseSlot);
      } else {
        renderFallbackAdBlock(inlineAd, "Blog Inline Ad");
      }
      blogList.appendChild(inlineAd);
    }
  });
}

function openCodePageAndRedirectCurrent(data, coupon) {
  const codePage = `/coupon-code.html?store=${encodeURIComponent(coupon.store)}&title=${encodeURIComponent(coupon.title)}&code=${encodeURIComponent(data.couponCode)}`;
  const codeWindow = window.open(codePage, "_blank", "noopener");
  if (!codeWindow) {
    alert("Please allow popups to open coupon code page.");
    return;
  }
  window.location.assign(data.affiliateUrl);
}

function renderCoupons(coupons) {
  couponList.innerHTML = "";

  if (!coupons.length) {
    couponList.innerHTML = `<article class="coupon-card"><p>No coupons match your search.</p></article>`;
    return;
  }

  coupons.forEach(coupon => {
    const node = couponTemplate.content.cloneNode(true);
    node.querySelector(".coupon-store").textContent = coupon.store;
    node.querySelector(".coupon-title").textContent = coupon.title;
    node.querySelector(".coupon-meta").textContent = `${coupon.expires} · ${coupon.category} · source: ${coupon.source}`;

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

    couponList.appendChild(node);
  });
}

async function refreshCoupons() {
  try {
    const coupons = await fetchCoupons();
    renderCoupons(coupons);
    renderStores(coupons);
  } catch (_) {
    couponList.innerHTML = `<article class="coupon-card"><p>Unable to load coupons right now.</p></article>`;
  }
}

async function refreshBlogs() {
  try {
    const blogs = await fetchBlogs();
    renderBlogs(blogs);
  } catch (_) {
    blogList.innerHTML = `<article class="blog-card"><div class="blog-content"><h3>Unable to load blogs right now.</h3></div></article>`;
  }
}

filterChips.addEventListener("click", event => {
  const chip = event.target.closest("button[data-filter]");
  if (!chip) return;
  activeFilter = chip.dataset.filter;
  filterChips.querySelectorAll("button").forEach(button => button.classList.toggle("active", button === chip));
  refreshCoupons();
});

searchForm.addEventListener("submit", event => {
  event.preventDefault();
  searchTerm = searchInput.value.trim().toLowerCase();
  refreshCoupons();
});

(async function init() {
  try {
    adSettings = await fetchAds();
  } catch (_) {
    adSettings = null;
  }
  try {
    const content = await fetchContent();
    applyHeroContent(content);
  } catch (_) {
    // keep defaults
  }
  applyHomeAds();
  await Promise.all([refreshCoupons(), refreshBlogs()]);
})();
