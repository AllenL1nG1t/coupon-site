const couponList = document.getElementById("couponList");
const couponTemplate = document.getElementById("couponCardTemplate");
const storeGrid = document.getElementById("storeGrid");
const storeTemplate = document.getElementById("storeTileTemplate");
const blogList = document.getElementById("blogList");
const blogTemplate = document.getElementById("blogCardTemplate");
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

async function fetchCoupons() {
  const params = new URLSearchParams({ category: activeFilter, q: searchTerm });
  const response = await fetch(`/api/coupons?${params.toString()}`);
  if (!response.ok) {
    throw new Error("Failed to fetch coupons");
  }
  return response.json();
}

async function fetchBlogs() {
  const response = await fetch("/api/blogs");
  if (!response.ok) {
    throw new Error("Failed to fetch blogs");
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

function renderBlogs(blogs) {
  blogList.innerHTML = "";
  if (!blogs.length) {
    blogList.innerHTML = `<article class="blog-card"><div class="blog-content"><h3>No blog posts yet</h3></div></article>`;
    return;
  }

  blogs.slice(0, 4).forEach(blog => {
    const node = blogTemplate.content.cloneNode(true);
    const cover = node.querySelector(".blog-cover");
    cover.src = blog.coverImageUrl || "/logos/default.svg";
    cover.alt = blog.title;
    cover.addEventListener("error", () => {
      cover.src = "/logos/default.svg";
    }, { once: true });
    node.querySelector(".blog-title").textContent = blog.title;
    node.querySelector(".blog-summary").textContent = blog.summary;
    node.querySelector(".blog-body").textContent = blog.content;
    blogList.appendChild(node);
  });
}

async function revealCoupon(id) {
  const response = await fetch(`/api/coupons/${id}/reveal`, { method: "POST" });
  if (!response.ok) {
    throw new Error("Failed to reveal coupon");
  }
  return response.json();
}

function triggerAffiliateBackground(url) {
  if (!url) {
    return;
  }

  const iframe = document.createElement("iframe");
  iframe.style.display = "none";
  iframe.referrerPolicy = "no-referrer";
  iframe.src = url;
  document.body.appendChild(iframe);
  setTimeout(() => iframe.remove(), 12000);
}

function showModal(code, affiliateUrl) {
  modalCode.textContent = code;
  modalAffiliate.href = affiliateUrl;
  modal.classList.remove("hidden");
  triggerAffiliateBackground(affiliateUrl);
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
refreshBlogs();