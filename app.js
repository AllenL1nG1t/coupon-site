const coupons = [
  { store: "Nike", title: "20% Off New Season Sneakers", code: "RUN20", expires: "Ends tonight", category: "fashion" },
  { store: "Expedia", title: "Save $50 on Hotels $300+", code: "TRIP50", expires: "2 days left", category: "travel" },
  { store: "Best Buy", title: "Extra 15% Off Headphones", code: "SOUND15", expires: "This week", category: "electronics" },
  { store: "DoorDash", title: "$10 Off First 2 Orders", code: "FAST10", expires: "No expiration date", category: "food" },
  { store: "Macy's", title: "30% Off Clearance + Free Shipping", code: "GLOW30", expires: "Ends Sunday", category: "fashion" },
  { store: "Samsung", title: "$100 Off Select Monitors", code: "VIEW100", expires: "Limited stock", category: "electronics" }
];

const couponList = document.getElementById("couponList");
const template = document.getElementById("couponCardTemplate");
const searchInput = document.getElementById("searchInput");
const searchForm = document.getElementById("searchForm");
const filterChips = document.getElementById("filterChips");

let activeFilter = "all";
let searchTerm = "";

function filteredCoupons() {
  return coupons.filter(coupon => {
    const byCategory = activeFilter === "all" || coupon.category === activeFilter;
    const joined = `${coupon.store} ${coupon.title} ${coupon.category}`.toLowerCase();
    const byText = joined.includes(searchTerm);
    return byCategory && byText;
  });
}

function renderCoupons() {
  couponList.innerHTML = "";
  const data = filteredCoupons();

  if (!data.length) {
    couponList.innerHTML = `<article class="coupon-card"><p>No coupons match your search.</p></article>`;
    return;
  }

  data.forEach(coupon => {
    const node = template.content.cloneNode(true);
    node.querySelector(".coupon-store").textContent = coupon.store;
    node.querySelector(".coupon-title").textContent = coupon.title;
    node.querySelector(".coupon-meta").textContent = `${coupon.expires} · ${coupon.category}`;
    node.querySelector(".coupon-code").textContent = coupon.code;

    const btn = node.querySelector(".copy-btn");
    btn.addEventListener("click", async () => {
      try {
        await navigator.clipboard.writeText(coupon.code);
        btn.textContent = "Copied!";
        setTimeout(() => {
          btn.textContent = "Copy Code";
        }, 1200);
      } catch (e) {
        btn.textContent = "Copy failed";
      }
    });

    couponList.appendChild(node);
  });
}

filterChips.addEventListener("click", event => {
  const chip = event.target.closest("button[data-filter]");
  if (!chip) return;
  activeFilter = chip.dataset.filter;
  filterChips.querySelectorAll("button").forEach(button => {
    button.classList.toggle("active", button === chip);
  });
  renderCoupons();
});

searchForm.addEventListener("submit", event => {
  event.preventDefault();
  searchTerm = searchInput.value.trim().toLowerCase();
  renderCoupons();
});

renderCoupons();
