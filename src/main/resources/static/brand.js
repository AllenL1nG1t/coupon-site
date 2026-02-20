const params = new URLSearchParams(window.location.search);
const store = params.get("store") || "";
const slug = params.get("slug") || "";

const brandTitle = document.getElementById("brandTitle");
const brandSummary = document.getElementById("brandSummary");
const brandDescription = document.getElementById("brandDescription");
const brandOfficial = document.getElementById("brandOfficial");
const brandHeroImage = document.getElementById("brandHeroImage");
const brandLogo = document.getElementById("brandLogo");

function fallbackView() {
  brandTitle.textContent = store || slug || "Brand";
  brandSummary.textContent = "Brand intro not configured yet. Please add it from Admin > Brands.";
  brandDescription.textContent = "This brand page supports custom hero image, summary, long description and official site URL, all managed in admin panel.";
  brandOfficial.href = "/admin-login.html";
  brandOfficial.textContent = "Go to Admin";
  brandHeroImage.src = "/logos/default.svg";
  brandLogo.src = "/logos/default.svg";
}

async function loadBrand() {
  let url = "";
  if (slug) url = `/api/brands/detail?slug=${encodeURIComponent(slug)}`;
  else if (store) url = `/api/brands/by-store?store=${encodeURIComponent(store)}`;
  else {
    fallbackView();
    return;
  }

  const response = await fetch(url);
  if (!response.ok) {
    fallbackView();
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
}

loadBrand().catch(fallbackView);