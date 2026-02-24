const params = new URLSearchParams(window.location.search);
const store = params.get("store") || "Brand";
const title = params.get("title") || "Offer";
const code = params.get("code") || "SEEDEAL";

const codeStore = document.getElementById("codeStore");
const codeTitle = document.getElementById("codeTitle");
const couponCode = document.getElementById("couponCode");
const copyCodeBtn = document.getElementById("copyCodeBtn");
const codeBrandLogo = document.getElementById("codeBrandLogo");
const codeTopAdWrap = document.getElementById("codeTopAdWrap");
const codeTopAd = document.getElementById("codeTopAd");
const codeBottomAdWrap = document.getElementById("codeBottomAdWrap");
const codeBottomAd = document.getElementById("codeBottomAd");

function normalizeTheme(theme) {
  const value = (theme || "").toLowerCase();
  if (value === "scheme-b" || value === "scheme-c") return value;
  return "scheme-a";
}

async function applyTheme() {
  try {
    const response = await fetch("/api/content/public");
    if (!response.ok) return;
    const content = await response.json();
    document.body.dataset.theme = normalizeTheme(content.themePreset);
  } catch (_) {
    // ignore
  }
}

async function applyBrandLogo() {
  try {
    const response = await fetch(`/api/brands/by-store?store=${encodeURIComponent(store)}`);
    if (response.ok) {
      const brand = await response.json();
      if (brand.logoUrl) {
        codeBrandLogo.src = brand.logoUrl;
        return;
      }
    }
  } catch (_) {
    // ignore
  }

  try {
    const couponRes = await fetch(`/api/coupons/by-store?store=${encodeURIComponent(store)}`);
    if (!couponRes.ok) return;
    const coupons = await couponRes.json();
    if (Array.isArray(coupons) && coupons.length > 0 && coupons[0].logoUrl) {
      codeBrandLogo.src = coupons[0].logoUrl;
    }
  } catch (_) {
    // ignore
  }
}

function renderCodeAdSlot(wrap, box, enabled, ads) {
  if (!wrap || !box) return;
  if (!enabled) {
    wrap.classList.add("hidden");
    box.innerHTML = "";
    return;
  }
  wrap.classList.remove("hidden");
  const title = ads.stripText || "Featured offer";
  const link = ads.stripLink || "#";
  box.innerHTML = `<a href="${link}" target="_blank" rel="noopener noreferrer" class="ad-box-fallback">${title}</a>`;
}

async function applyCodeAds() {
  try {
    const response = await fetch("/api/ads/public");
    if (!response.ok) return;
    const ads = await response.json();
    renderCodeAdSlot(codeTopAdWrap, codeTopAd, !!ads.codeTopEnabled, ads);
    renderCodeAdSlot(codeBottomAdWrap, codeBottomAd, !!ads.codeBottomEnabled, ads);
  } catch (_) {
    // ignore
  }
}

codeStore.textContent = store;
codeTitle.textContent = title;
couponCode.textContent = code;
codeBrandLogo.addEventListener("error", () => { codeBrandLogo.src = "/logos/default.svg"; }, { once: true });

copyCodeBtn.addEventListener("click", async () => {
  try {
    await navigator.clipboard.writeText(code);
    copyCodeBtn.textContent = "Copied";
    setTimeout(() => copyCodeBtn.textContent = "Copy Code", 1200);
  } catch (_) {
    copyCodeBtn.textContent = "Copy failed";
  }
});

(async function init() {
  await Promise.all([applyTheme(), applyCodeAds(), applyBrandLogo()]);
})();
