const params = new URLSearchParams(window.location.search);
const store = params.get("store") || "";
const slug = params.get("slug") || "";

const brandTitle = document.getElementById("brandTitle");
const brandSummary = document.getElementById("brandSummary");
const brandDescription = document.getElementById("brandDescription");
const brandOfficial = document.getElementById("brandOfficial");
const brandHeroImage = document.getElementById("brandHeroImage");
const brandLogo = document.getElementById("brandLogo");
const brandCouponList = document.getElementById("brandCouponList");
const brandCrumb = document.getElementById("brandCrumb");
const brandCouponCount = document.getElementById("brandCouponCount");
const brandTopCategory = document.getElementById("brandTopCategory");
const brandStoreName = document.getElementById("brandStoreName");
const brandCouponHint = document.getElementById("brandCouponHint");

function normalizeTheme(theme) {
  const value = (theme || "").toLowerCase();
  if (value === "scheme-b" || value === "scheme-c") return value;
  return "scheme-a";
}

async function applyThemeFromContent() {
  try {
    const response = await fetch("/api/content/public");
    if (!response.ok) return;
    const content = await response.json();
    document.body.dataset.theme = normalizeTheme(content.themePreset);
    const brandTop = document.querySelector(".brand");
    if (brandTop) {
      brandTop.textContent = content.siteName || "Dotiki Coupon";
    }
  } catch (_) {
    // ignore
  }
}

async function applySeo() {
  try {
    const response = await fetch("/api/seo/public");
    if (!response.ok) return;
    const seo = await response.json();
    if (seo.title) document.title = seo.title;
  } catch (_) {
    // ignore
  }
}

function fallbackView() {
  const label = store || slug || "Brand";
  brandTitle.textContent = label;
  brandCrumb.textContent = label;
  brandStoreName.textContent = label;
  brandSummary.textContent = "Brand intro not configured yet. Please add it from Admin > Brands.";
  brandDescription.textContent = "This brand page supports custom hero image, summary, long description and official site URL, all managed in admin panel.";
  brandOfficial.href = "/admin-login.html";
  brandOfficial.textContent = "Go to Admin";
  brandHeroImage.src = "/logos/default.svg";
  brandLogo.src = "/logos/default.svg";
}

async function revealCoupon(id) {
  const response = await fetch(`/api/coupons/${id}/reveal`, { method: "POST" });
  if (!response.ok) throw new Error("Failed to reveal coupon");
  return response.json();
}

function openCodePageAndRedirectCurrent(data, coupon) {
  const codePage = `/coupon-code.html?store=${encodeURIComponent(coupon.store)}&title=${encodeURIComponent(coupon.title)}&code=${encodeURIComponent(data.couponCode)}`;
  if (data.affiliateUrl) {
    window.open(data.affiliateUrl, "_blank", "noopener");
  }
  window.location.assign(codePage);
}

function renderCoupons(coupons) {
  brandCouponCount.textContent = String(coupons.length);
  if (!coupons.length) {
    brandTopCategory.textContent = "-";
    brandCouponHint.textContent = "No matched coupons yet";
    brandCouponList.innerHTML = `<article class="coupon-card"><p>No coupons available for this brand yet.</p></article>`;
    return;
  }

  const categoryCount = new Map();
  coupons.forEach(c => categoryCount.set(c.category, (categoryCount.get(c.category) || 0) + 1));
  const topCategory = Array.from(categoryCount.entries()).sort((a, b) => b[1] - a[1])[0]?.[0] || "-";
  brandTopCategory.textContent = topCategory;
  const activeCoupons = coupons.filter(c => !c.expired);
  const expiredCoupons = coupons.filter(c => c.expired);
  brandCouponHint.textContent = `${activeCoupons.length} active coupons / ${expiredCoupons.length} expired`;

  const renderGroup = (list, title, expired = false) => `
    <h3 style="margin:10px 0 6px;">${title}</h3>
    ${list.map(coupon => `
    <article class="coupon-card" data-coupon-id="${coupon.id}" data-store="${coupon.store}" data-title="${coupon.title}">
      <div>
        <p class="coupon-store">${coupon.store}</p>
        <h3 class="coupon-title">${coupon.title}</h3>
        <p class="coupon-meta ${expired ? "expired" : ""}">${coupon.expires} · ${coupon.category} · clicks: ${coupon.clickCount ?? 0}</p>
      </div>
      <div class="coupon-actions">
        <button class="reveal-btn" data-reveal-id="${coupon.id}">Show Coupon Code</button>
      </div>
    </article>
  `).join("")}
  `;
  brandCouponList.innerHTML = `${renderGroup(activeCoupons, "Active Coupons")}${expiredCoupons.length ? renderGroup(expiredCoupons, "Expired Coupons", true) : ""}`;

  brandCouponList.querySelectorAll("[data-reveal-id]").forEach(btn => {
    btn.addEventListener("click", async () => {
      btn.disabled = true;
      btn.textContent = "Loading...";
      try {
        const card = btn.closest("[data-coupon-id]");
        const coupon = {
          id: Number(card.dataset.couponId),
          store: card.dataset.store,
          title: card.dataset.title
        };
        const data = await revealCoupon(coupon.id);
        btn.textContent = "Redirecting...";
        openCodePageAndRedirectCurrent(data, coupon);
      } catch (_) {
        btn.textContent = "Try again";
        btn.disabled = false;
      }
    });
  });
}

async function fetchCouponsByCandidates(candidates) {
  const seen = new Set();
  const merged = [];
  const normalizedCandidates = candidates
    .filter(Boolean)
    .map(value => value.toLowerCase().replace(/[^a-z0-9]/g, ""));

  for (const candidate of candidates) {
    if (!candidate) continue;
    const response = await fetch(`/api/coupons/by-store?store=${encodeURIComponent(candidate)}`);
    if (!response.ok) continue;
    const data = await response.json();
    data.forEach(coupon => {
      if (seen.has(coupon.id)) return;
      seen.add(coupon.id);
      merged.push(coupon);
    });
  }

  if (!merged.length && normalizedCandidates.length) {
    const allResponse = await fetch("/api/coupons?category=all&q=");
    if (allResponse.ok) {
      const allCoupons = await allResponse.json();
      allCoupons.forEach(coupon => {
        const key = (coupon.store || "").toLowerCase().replace(/[^a-z0-9]/g, "");
        const matched = normalizedCandidates.some(candidate => key.includes(candidate) || candidate.includes(key));
        if (!matched || seen.has(coupon.id)) return;
        seen.add(coupon.id);
        merged.push(coupon);
      });
    }
  }

  return merged;
}

async function loadBrand() {
  let url = "";
  if (slug) url = `/api/brands/detail?slug=${encodeURIComponent(slug)}`;
  else if (store) url = `/api/brands/by-store?store=${encodeURIComponent(store)}`;
  else {
    fallbackView();
    renderCoupons([]);
    return;
  }

  const response = await fetch(url);
  if (!response.ok) {
    fallbackView();
    const coupons = await fetchCouponsByCandidates([store, slug]);
    renderCoupons(coupons);
    return;
  }

  const data = await response.json();
  brandTitle.textContent = data.title;
  brandCrumb.textContent = data.storeName;
  brandStoreName.textContent = data.storeName;
  brandSummary.textContent = data.summary;
  brandDescription.textContent = data.description;
  brandOfficial.href = data.officialUrl;
  brandOfficial.textContent = `Visit ${data.storeName}`;
  brandHeroImage.src = data.heroImageUrl || "/logos/default.svg";
  brandLogo.src = data.logoUrl || "/logos/default.svg";

  brandHeroImage.addEventListener("error", () => { brandHeroImage.src = "/logos/default.svg"; }, { once: true });
  brandLogo.addEventListener("error", () => { brandLogo.src = "/logos/default.svg"; }, { once: true });

  const coupons = await fetchCouponsByCandidates([data.storeName, store, slug]);
  renderCoupons(coupons);
}

(async function init() {
  await applyThemeFromContent();
  await applySeo();
  try {
    await loadBrand();
  } catch (_) {
    fallbackView();
    const coupons = await fetchCouponsByCandidates([store, slug]);
    renderCoupons(coupons);
  }
})();
