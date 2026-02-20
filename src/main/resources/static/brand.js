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

function fallbackView() {
  brandTitle.textContent = store || slug || "Brand";
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
  window.open(codePage, "_blank", "noopener");
  window.location.assign(data.affiliateUrl);
}

function renderCoupons(coupons) {
  if (!coupons.length) {
    brandCouponList.innerHTML = `<article class="coupon-card"><p>No coupons available for this brand yet.</p></article>`;
    return;
  }

  brandCouponList.innerHTML = coupons.map(coupon => `
    <article class="coupon-card" data-coupon-id="${coupon.id}" data-store="${coupon.store}" data-title="${coupon.title}">
      <div>
        <p class="coupon-store">${coupon.store}</p>
        <h3 class="coupon-title">${coupon.title}</h3>
        <p class="coupon-meta">${coupon.expires} · ${coupon.category} · source: ${coupon.source}</p>
      </div>
      <div class="coupon-actions">
        <button class="reveal-btn" data-reveal-id="${coupon.id}">Show Coupon Code</button>
      </div>
    </article>
  `).join("");

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
    renderCoupons([]);
    return;
  }

  const data = await response.json();
  brandTitle.textContent = data.title;
  brandSummary.textContent = data.summary;
  brandDescription.textContent = data.description;
  brandOfficial.href = data.officialUrl;
  brandOfficial.textContent = `Visit ${data.storeName}`;
  brandHeroImage.src = data.heroImageUrl || "/logos/default.svg";
  brandLogo.src = data.logoUrl || "/logos/default.svg";

  brandHeroImage.addEventListener("error", () => { brandHeroImage.src = "/logos/default.svg"; }, { once: true });
  brandLogo.addEventListener("error", () => { brandLogo.src = "/logos/default.svg"; }, { once: true });

  const couponResponse = await fetch(`/api/coupons/by-store?store=${encodeURIComponent(data.storeName)}`);
  if (!couponResponse.ok) {
    renderCoupons([]);
    return;
  }

  const coupons = await couponResponse.json();
  renderCoupons(coupons);
}

loadBrand().catch(() => {
  fallbackView();
  renderCoupons([]);
});
