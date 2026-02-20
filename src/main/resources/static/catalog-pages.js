const pageMode = document.body.dataset.page;
const container = document.getElementById("pageContainer");
const searchInput = document.getElementById("searchInput");
const searchForm = document.getElementById("searchForm");

let query = "";

async function fetchCoupons(category = "all") {
  const params = new URLSearchParams({ category, q: query });
  const response = await fetch(`/api/coupons?${params.toString()}`);
  if (!response.ok) throw new Error("Failed to fetch coupons");
  return response.json();
}

async function revealCoupon(id) {
  const response = await fetch(`/api/coupons/${id}/reveal`, { method: "POST" });
  if (!response.ok) throw new Error("Failed to reveal coupon");
  return response.json();
}

function openCodePageAndRedirectCurrent(data, store, title) {
  const codePage = `/coupon-code.html?store=${encodeURIComponent(store)}&title=${encodeURIComponent(title)}&code=${encodeURIComponent(data.couponCode)}`;
  window.open(codePage, "_blank", "noopener");
  window.location.assign(data.affiliateUrl);
}

function uniqueStores(coupons) {
  const seen = new Set();
  return coupons.filter(c => {
    if (seen.has(c.store)) return false;
    seen.add(c.store);
    return true;
  });
}

function categoriesFromCoupons(coupons) {
  return Array.from(new Set(coupons.map(c => c.category))).sort();
}

function couponCard(coupon) {
  return `
    <article class="coupon-card">
      <div>
        <p class="coupon-store">${coupon.store}</p>
        <h3 class="coupon-title">${coupon.title}</h3>
        <p class="coupon-meta">${coupon.expires} · ${coupon.category} · source: ${coupon.source}</p>
      </div>
      <div class="coupon-actions">
        <button class="reveal-btn" data-reveal-id="${coupon.id}" data-store="${coupon.store}" data-title="${coupon.title}">Show Coupon Code</button>
      </div>
    </article>
  `;
}

async function bindRevealButtons() {
  container.querySelectorAll("[data-reveal-id]").forEach(btn => {
    btn.addEventListener("click", async () => {
      btn.disabled = true;
      btn.textContent = "Loading...";
      try {
        const store = btn.dataset.store || "";
        const title = btn.dataset.title || "";
        const data = await revealCoupon(Number(btn.dataset.revealId));
        btn.textContent = "Redirecting...";
        openCodePageAndRedirectCurrent(data, store, title);
      } catch (_) {
        btn.textContent = "Try again";
        btn.disabled = false;
      }
    });
  });
}

async function renderStoresPage() {
  const coupons = await fetchCoupons("all");
  const stores = uniqueStores(coupons);
  container.innerHTML = `
    <section class="section-head">
      <h2>All Stores</h2>
      <span class="muted-inline">${stores.length} stores</span>
    </section>
    <div class="store-grid">
      ${stores.map(store => `
        <a class="store-link" href="/brand.html?store=${encodeURIComponent(store.store)}">
          <article class="store-tile">
            <img class="store-logo" src="${store.logoUrl}" alt="${store.store} logo" onerror="this.src='/logos/default.svg'">
            <p class="store-name">${store.store}</p>
          </article>
        </a>
      `).join("")}
    </div>
  `;
}

async function renderCategoriesPage() {
  const allCoupons = await fetchCoupons("all");
  const categories = categoriesFromCoupons(allCoupons);

  container.innerHTML = `
    <section class="section-head">
      <h2>Categories</h2>
      <span class="muted-inline">Click a category to filter coupons</span>
    </section>
    <div class="chips" id="categoryChips">
      <button data-category="all" class="active">All</button>
      ${categories.map(c => `<button data-category="${c}">${c}</button>`).join("")}
    </div>
    <div class="coupon-list" id="categoryCouponList"></div>
  `;

  const list = document.getElementById("categoryCouponList");
  const chips = document.getElementById("categoryChips");

  async function loadList(category) {
    const coupons = await fetchCoupons(category);
    list.innerHTML = coupons.length ? coupons.map(couponCard).join("") : `<article class="coupon-card"><p>No coupons in this category.</p></article>`;
    await bindRevealButtons();
  }

  chips.addEventListener("click", async event => {
    const btn = event.target.closest("button[data-category]");
    if (!btn) return;
    chips.querySelectorAll("button").forEach(node => node.classList.toggle("active", node === btn));
    await loadList(btn.dataset.category);
  });

  await loadList("all");
}

async function renderCashbackPage() {
  const allCoupons = await fetchCoupons("all");
  const groups = new Map();

  allCoupons.forEach(coupon => {
    const list = groups.get(coupon.category) || [];
    if (list.length < 3) {
      list.push(coupon);
    }
    groups.set(coupon.category, list);
  });

  const cards = Array.from(groups.entries()).map(([category, coupons]) => `
    <article class="cashback-card">
      <h3>${category}</h3>
      <p>Popular offers and cashback routes:</p>
      <ul>
        ${coupons.map(c => `<li><a href="/brand.html?store=${encodeURIComponent(c.store)}">${c.store}</a> - ${c.title}</li>`).join("")}
      </ul>
    </article>
  `).join("");

  container.innerHTML = `
    <section class="section-head">
      <h2>Cash Back Directory</h2>
      <span class="muted-inline">Category based saving paths</span>
    </section>
    <div class="cashback-grid">${cards || "<article class='cashback-card'><p>No cashback offers available.</p></article>"}</div>
  `;
}

searchForm.addEventListener("submit", async event => {
  event.preventDefault();
  query = searchInput.value.trim().toLowerCase();
  if (pageMode === "stores") await renderStoresPage();
  if (pageMode === "categories") await renderCategoriesPage();
  if (pageMode === "cashback") await renderCashbackPage();
});

(async function init() {
  try {
    if (pageMode === "stores") await renderStoresPage();
    if (pageMode === "categories") await renderCategoriesPage();
    if (pageMode === "cashback") await renderCashbackPage();
  } catch (_) {
    container.innerHTML = `<article class="coupon-card"><p>Unable to load page data right now.</p></article>`;
  }
})();
