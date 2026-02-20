const loginBtn = document.getElementById("loginBtn");
const username = document.getElementById("username");
const password = document.getElementById("password");
const statusEl = document.getElementById("status");

async function tryLogin() {
  statusEl.textContent = "Logging in...";
  const response = await fetch("/api/admin/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: username.value.trim(), password: password.value })
  });

  if (!response.ok) {
    statusEl.textContent = "Login failed. Check credentials.";
    return;
  }

  window.location.href = "/admin-panel.html";
}

loginBtn.addEventListener("click", tryLogin);
password.addEventListener("keydown", event => {
  if (event.key === "Enter") {
    tryLogin();
  }
});