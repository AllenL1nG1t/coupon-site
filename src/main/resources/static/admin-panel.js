const crawlerEnabled = document.getElementById("crawlerEnabled");
const saveCrawlerBtn = document.getElementById("saveCrawlerBtn");
const runCrawlerBtn = document.getElementById("runCrawlerBtn");
const crawlerStatus = document.getElementById("crawlerStatus");
const logPanel = document.getElementById("logPanel");
const couponTable = document.getElementById("couponTable");
const blogTable = document.getElementById("blogTable");
const logoutBtn = document.getElementById("logoutBtn");

const couponStore = document.getElementById("couponStore");
const couponTitle = document.getElementById("couponTitle");
const couponCategory = document.getElementById("couponCategory");
const couponExpires = document.getElementById("couponExpires");
const couponCode = document.getElementById("couponCode");
const couponAffiliate = document.getElementById("couponAffiliate");
const couponLogo = document.getElementById("couponLogo");
const couponSource = document.getElementById("couponSource");
const saveCouponBtn = document.getElementById("saveCouponBtn");

const blogTitle = document.getElementById("blogTitle");
const blogSummary = document.getElementById("blogSummary");
const blogCover = document.getElementById("blogCover");
const blogPublished = document.getElementById("blogPublished");
const blogContent = document.getElementById("blogContent");
const saveBlogBtn = document.getElementById("saveBlogBtn");
const blogImageFile = document.getElementById("blogImageFile");
const uploadImageBtn = document.getElementById("uploadImageBtn");
const blogStatus = document.getElementById("blogStatus");

async function adminFetch(url, options = {}) {
  const response = await fetch(url, options);
  if (response.status === 401) {
    window.location.href = "/admin-login.html";
    throw new Error("Unauthorized");
  }
  return response;
}

async function checkAuth() {
  const response = await fetch("/api/admin/auth/status");
  if (!response.ok) {
    window.location.href = "/admin-login.html";
    return false;
  }
  const data = await response.json();
  if (!data.loggedIn) {
    window.location.href = "/admin-login.html";
    return false;
  }
  return true;
}

async function loadCrawler() {
  const settingsRes = await adminFetch("/api/admin/settings");
  const settings = await settingsRes.json();
  crawlerEnabled.checked = settings.crawlerEnabled;

  const logsRes = await adminFetch("/api/admin/logs");
  const logs = await logsRes.json();
  logPanel.textContent = (logs || []).map(log => `[${log.createdAt}] [${log.level}] ${log.message}`).join("\n") || "No logs yet";
}

async function saveCrawler() {
  crawlerStatus.textContent = "Saving...";
  const response = await adminFetch("/api/admin/settings", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ crawlerEnabled: crawlerEnabled.checked })
  });
  crawlerStatus.textContent = response.ok ? "Saved" : "Save failed";
}

async function runCrawler() {
  crawlerStatus.textContent = "Running...";
  const response = await adminFetch("/api/admin/crawler/run", { method: "POST" });
  crawlerStatus.textContent = await response.text();
  await loadCrawler();
}

function renderCouponRows(coupons) {
  couponTable.innerHTML = `
    <thead>
      <tr><th>ID</th><th>Store</th><th>Title</th><th>Category</th><th>Code</th><th>Affiliate URL</th><th>Actions</th></tr>
    </thead>
    <tbody>
      ${coupons.map(c => `
        <tr>
          <td>${c.id}</td>
          <td>${c.store}</td>
          <td>${c.title}</td>
          <td>${c.category}</td>
          <td>${c.couponCode}</td>
          <td style="max-width:260px;word-break:break-all;">${c.affiliateUrl}</td>
          <td>
            <button class="admin-mini-btn" data-edit-coupon="${c.id}">Edit</button>
            <button class="admin-mini-btn" data-del-coupon="${c.id}">Delete</button>
          </td>
        </tr>
      `).join("")}
    </tbody>
  `;

  couponTable.querySelectorAll("[data-del-coupon]").forEach(btn => {
    btn.addEventListener("click", async () => {
      await adminFetch(`/api/admin/coupons?id=${btn.dataset.delCoupon}`, { method: "DELETE" });
      await loadCoupons();
    });
  });

  couponTable.querySelectorAll("[data-edit-coupon]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const id = Number(btn.dataset.editCoupon);
      const response = await adminFetch("/api/admin/coupons");
      const couponsData = await response.json();
      const item = couponsData.find(x => x.id === id);
      if (!item) return;
      couponStore.value = item.store;
      couponTitle.value = item.title;
      couponCategory.value = item.category;
      couponExpires.value = item.expires;
      couponCode.value = item.couponCode;
      couponAffiliate.value = item.affiliateUrl;
      couponLogo.value = item.logoUrl;
      couponSource.value = item.source;
      saveCouponBtn.dataset.editId = String(item.id);
      saveCouponBtn.textContent = "Update Coupon";
    });
  });
}

async function loadCoupons() {
  const response = await adminFetch("/api/admin/coupons");
  renderCouponRows(await response.json());
}

async function saveCoupon() {
  const body = {
    id: saveCouponBtn.dataset.editId ? Number(saveCouponBtn.dataset.editId) : null,
    store: couponStore.value,
    title: couponTitle.value,
    category: couponCategory.value,
    expires: couponExpires.value,
    couponCode: couponCode.value,
    affiliateUrl: couponAffiliate.value,
    logoUrl: couponLogo.value,
    source: couponSource.value || "admin"
  };
  await adminFetch("/api/admin/coupons", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  saveCouponBtn.dataset.editId = "";
  saveCouponBtn.textContent = "Add Coupon";
  [couponStore, couponTitle, couponCategory, couponExpires, couponCode, couponAffiliate, couponLogo, couponSource].forEach(el => el.value = "");
  await loadCoupons();
}

function renderBlogRows(blogs) {
  blogTable.innerHTML = `
    <thead>
      <tr><th>ID</th><th>Title</th><th>Published</th><th>Cover</th><th>Actions</th></tr>
    </thead>
    <tbody>
      ${blogs.map(b => `
        <tr>
          <td>${b.id}</td>
          <td>${b.title}</td>
          <td>${b.published}</td>
          <td style="max-width:220px;word-break:break-all;">${b.coverImageUrl}</td>
          <td>
            <button class="admin-mini-btn" data-edit-blog="${b.id}">Edit</button>
            <button class="admin-mini-btn" data-del-blog="${b.id}">Delete</button>
          </td>
        </tr>
      `).join("")}
    </tbody>
  `;

  blogTable.querySelectorAll("[data-del-blog]").forEach(btn => {
    btn.addEventListener("click", async () => {
      await adminFetch(`/api/admin/blogs?id=${btn.dataset.delBlog}`, { method: "DELETE" });
      await loadBlogs();
    });
  });

  blogTable.querySelectorAll("[data-edit-blog]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const id = Number(btn.dataset.editBlog);
      const response = await adminFetch("/api/admin/blogs");
      const blogsData = await response.json();
      const item = blogsData.find(x => x.id === id);
      if (!item) return;
      blogTitle.value = item.title;
      blogSummary.value = item.summary;
      blogCover.value = item.coverImageUrl;
      blogContent.value = item.content;
      blogPublished.value = String(item.published);
      saveBlogBtn.dataset.editId = String(item.id);
      saveBlogBtn.textContent = "Update Blog";
    });
  });
}

async function loadBlogs() {
  const response = await adminFetch("/api/admin/blogs");
  renderBlogRows(await response.json());
}

async function saveBlog() {
  const body = {
    id: saveBlogBtn.dataset.editId ? Number(saveBlogBtn.dataset.editId) : null,
    title: blogTitle.value,
    summary: blogSummary.value,
    content: blogContent.value,
    coverImageUrl: blogCover.value,
    published: blogPublished.value === "true"
  };
  await adminFetch("/api/admin/blogs", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  saveBlogBtn.dataset.editId = "";
  saveBlogBtn.textContent = "Add Blog";
  [blogTitle, blogSummary, blogCover, blogContent].forEach(el => el.value = "");
  blogPublished.value = "true";
  await loadBlogs();
}

async function uploadBlogImage() {
  if (!blogImageFile.files.length) {
    blogStatus.textContent = "Choose an image file first";
    return;
  }
  const formData = new FormData();
  formData.append("file", blogImageFile.files[0]);
  const response = await adminFetch("/api/admin/uploads/images", {
    method: "POST",
    body: formData
  });
  if (!response.ok) {
    blogStatus.textContent = "Upload failed";
    return;
  }
  const data = await response.json();
  blogCover.value = data.url;
  blogStatus.textContent = `Uploaded: ${data.url}`;
}

async function logout() {
  await adminFetch("/api/admin/auth/logout", { method: "POST" });
  window.location.href = "/admin-login.html";
}

saveCrawlerBtn.addEventListener("click", saveCrawler);
runCrawlerBtn.addEventListener("click", runCrawler);
saveCouponBtn.addEventListener("click", saveCoupon);
saveBlogBtn.addEventListener("click", saveBlog);
uploadImageBtn.addEventListener("click", uploadBlogImage);
logoutBtn.addEventListener("click", event => { event.preventDefault(); logout(); });

(async function init() {
  const ok = await checkAuth();
  if (!ok) return;
  await loadCrawler();
  await loadCoupons();
  await loadBlogs();
})();