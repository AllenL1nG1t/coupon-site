const params = new URLSearchParams(window.location.search);
const store = params.get("store") || "Brand";
const title = params.get("title") || "Offer";
const code = params.get("code") || "SEEDEAL";

const codeStore = document.getElementById("codeStore");
const codeTitle = document.getElementById("codeTitle");
const couponCode = document.getElementById("couponCode");
const copyCodeBtn = document.getElementById("copyCodeBtn");

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