const crawlerEnabled = document.getElementById("crawlerEnabled");
const saveSettings = document.getElementById("saveSettings");
const runCrawler = document.getElementById("runCrawler");
const refreshLogs = document.getElementById("refreshLogs");
const logPanel = document.getElementById("logPanel");
const statusText = document.getElementById("statusText");

async function loadDashboard() {
  const response = await fetch("/api/admin/dashboard");
  if (!response.ok) {
    throw new Error("Failed to load dashboard");
  }

  const data = await response.json();
  crawlerEnabled.checked = data.crawlerEnabled;
  renderLogs(data.logs || []);
}

function renderLogs(logs) {
  if (!logs.length) {
    logPanel.textContent = "No crawler logs yet.";
    return;
  }

  logPanel.textContent = logs
    .map(log => `[${log.createdAt}] [${log.level}] ${log.message}`)
    .join("\n");
}

async function saveCrawlerSetting() {
  statusText.textContent = "Saving...";
  const response = await fetch("/api/admin/settings", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ crawlerEnabled: crawlerEnabled.checked })
  });

  if (!response.ok) {
    statusText.textContent = "Save failed";
    return;
  }

  statusText.textContent = "Saved";
}

async function runCrawlerNow() {
  statusText.textContent = "Crawler running...";
  const response = await fetch("/api/admin/crawler/run", { method: "POST" });
  const text = await response.text();
  statusText.textContent = response.ok ? text : "Crawler failed";
  await reloadLogs();
}

async function reloadLogs() {
  const response = await fetch("/api/admin/logs");
  if (!response.ok) {
    logPanel.textContent = "Failed to load logs";
    return;
  }
  const logs = await response.json();
  renderLogs(logs || []);
}

saveSettings.addEventListener("click", saveCrawlerSetting);
runCrawler.addEventListener("click", runCrawlerNow);
refreshLogs.addEventListener("click", reloadLogs);

loadDashboard().catch(() => {
  statusText.textContent = "Failed to load admin data";
});

