const couponList = document.getElementById("couponList");
const couponTemplate = document.getElementById("couponCardTemplate");
const storeGrid = document.getElementById("storeGrid");
const storeTemplate = document.getElementById("storeTileTemplate");
const searchInput = document.getElementById("searchInput");
const searchForm = document.getElementById("searchForm");
const filterChips = document.getElementById("filterChips");
const modal = document.getElementById("couponModal");
const modalCode = document.getElementById("modalCode");
const modalAffiliate = document.getElementById("modalAffiliate");
const copyModalCode = document.getElementById("copyModalCode");
const closeModal = document.getElementById("closeModal");

let activeFilter = "all";
let searchTerm = "";
let latestCoupons = [];

async function fetchCoupons() {
  const params = new URLSearchParams({ category: activeFilter, q: searchTerm });
  const response = await fetch(`/api/coupons?${params.toString()}`);
  if (!response.ok) {
    throw new Error("Failed to fetch coupons");
  }
  return response.json();
}

function renderStores(coupons) {
  const stores = [];
  const seen = new Set();

  coupons.forEach(coupon => {
    if (seen.has(coupon.store)) {
      return;
    }
    seen.add(coupon.store);
    stores.push(coupon);
  });

  storeGrid.innerHTML = "";
  stores.slice(0, 8).forEach(store => {
    const node = storeTemplate.content.cloneNode(true);
    const logo = node.querySelector(".store-logo");
    logo.src = store.logoUrl;
    logo.alt = `${store.store} logo`;
    logo.addEventListener("error", () => {
      logo.src = "/logos/default.svg";
    }, { once: true });
    node.querySelector(".store-name").textContent = store.store;
    storeGrid.appendChild(node);
  });
}

async function revealCoupon(id) {
  const response = await fetch(`/api/coupons/${id}/reveal`, { method: "POST" });
  if (!response.ok) {
    throw new Error("Failed to reveal coupon");
  }
  return response.json();
}

function showModal(code, affiliateUrl) {
  modalCode.textContent = code;
  modalAffiliate.href = affiliateUrl;
  modal.classList.remove("hidden");

  const newTab = window.open(affiliateUrl, "_blank", "noopener,noreferrer");
  if (newTab) {
    window.focus();
  }
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
        showModal(data.couponCode, data.affiliateUrl);
        btn.textContent = "Show Coupon Code";
      } catch (_) {
        btn.textContent = "Try again";
      } finally {
        btn.disabled = false;
      }
    });

    couponList.appendChild(node);
  });
}

async function refreshCoupons() {
  try {
    latestCoupons = await fetchCoupons();
    renderCoupons(latestCoupons);
    renderStores(latestCoupons);
  } catch (_) {
    couponList.innerHTML = `<article class="coupon-card"><p>Unable to load coupons right now.</p></article>`;
  }
}

filterChips.addEventListener("click", event => {
  const chip = event.target.closest("button[data-filter]");
  if (!chip) return;

  activeFilter = chip.dataset.filter;
  filterChips.querySelectorAll("button").forEach(button => {
    button.classList.toggle("active", button === chip);
  });

  refreshCoupons();
});

searchForm.addEventListener("submit", event => {
  event.preventDefault();
  searchTerm = searchInput.value.trim().toLowerCase();
  refreshCoupons();
});

closeModal.addEventListener("click", () => {
  modal.classList.add("hidden");
});

copyModalCode.addEventListener("click", async () => {
  try {
    await navigator.clipboard.writeText(modalCode.textContent || "");
    copyModalCode.textContent = "Copied";
    setTimeout(() => {
      copyModalCode.textContent = "Copy Code";
    }, 1200);
  } catch (_) {
    copyModalCode.textContent = "Copy Failed";
  }
});

modal.addEventListener("click", event => {
  if (event.target === modal) {
    modal.classList.add("hidden");
  }
});

refreshCoupons();


