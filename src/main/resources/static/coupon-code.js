const params = new URLSearchParams(window.location.search);
const store = params.get("store") || "Brand";
const title = params.get("title") || "Offer";
const code = params.get("code") || "SEEDEAL";

const codeStore = document.getElementById("codeStore");
const codeTitle = document.getElementById("codeTitle");
const couponCode = document.getElementById("couponCode");
const copyCodeBtn = document.getElementById("copyCodeBtn");

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

codeStore.textContent = store;
codeTitle.textContent = title;
couponCode.textContent = code;

copyCodeBtn.addEventListener("click", async () => {
  try {
    await navigator.clipboard.writeText(code);
    copyCodeBtn.textContent = "Copied";
    setTimeout(() => copyCodeBtn.textContent = "Copy Code", 1200);
  } catch (_) {
    copyCodeBtn.textContent = "Copy failed";
  }
});

applyTheme();
