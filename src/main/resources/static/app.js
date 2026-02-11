const state = {
  token: null,
  page: 0,
  size: 5,
  lastPage: 0,
};

const apiBase = "";

function setAuthStatus() {
  const el = document.getElementById("auth-status");
  const logoutBtn = document.getElementById("logout-btn");
  if (state.token) {
    el.textContent = "Authenticated (JWT in use)";
    logoutBtn.classList.remove("hidden");
  } else {
    el.textContent = "Not logged in";
    logoutBtn.classList.add("hidden");
  }
}

function setMessage(id, text, isError = false) {
  const el = document.getElementById(id);
  el.textContent = text || "";
  el.classList.remove("error", "success");
  if (!text) return;
  el.classList.add(isError ? "error" : "success");
}

async function api(path, options = {}) {
  const headers = options.headers || {};
  headers["Content-Type"] = "application/json";
  if (state.token) {
    headers["Authorization"] = `Bearer ${state.token}`;
  }
  const res = await fetch(apiBase + path, { ...options, headers });
  if (!res.ok) {
    let msg = `Error ${res.status}`;
    try {
      const body = await res.json();
      msg = body.error || JSON.stringify(body);
    } catch {
      // ignore
    }
    throw new Error(msg);
  }
  if (res.status === 204) return null;
  return res.json();
}

function setupTabs() {
  const buttons = document.querySelectorAll(".tab-button");
  buttons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const tab = btn.dataset.tab;
      buttons.forEach((b) => b.classList.remove("active"));
      document
        .querySelectorAll(".tab-content")
        .forEach((c) => c.classList.remove("active"));
      btn.classList.add("active");
      document.getElementById(tab).classList.add("active");
    });
  });
}

function setupAuth() {
  document
    .getElementById("login-form")
    .addEventListener("submit", async (e) => {
      e.preventDefault();
      setMessage("auth-message", "");
      try {
        const body = {
          username: document.getElementById("login-username").value.trim(),
          password: document.getElementById("login-password").value,
        };
        const res = await api("/api/auth/login", {
          method: "POST",
          body: JSON.stringify(body),
        });
        state.token = res.token;
        setAuthStatus();
        setMessage("auth-message", "Login successful", false);
        await Promise.all([loadDepartments(), loadEmployees()]);
      } catch (err) {
        setMessage("auth-message", err.message, true);
      }
    });

  document
    .getElementById("register-form")
    .addEventListener("submit", async (e) => {
      e.preventDefault();
      setMessage("auth-message", "");
      try {
        const body = {
          username: document
            .getElementById("register-username")
            .value.trim(),
          password: document.getElementById("register-password").value,
          role: document.getElementById("register-role").value,
        };
        const res = await api("/api/auth/register", {
          method: "POST",
          body: JSON.stringify(body),
        });
        state.token = res.token;
        setAuthStatus();
        setMessage("auth-message", "Registered and logged in", false);
        await Promise.all([loadDepartments(), loadEmployees()]);
      } catch (err) {
        setMessage("auth-message", err.message, true);
      }
    });

  document.getElementById("logout-btn").addEventListener("click", () => {
    state.token = null;
    setAuthStatus();
    setMessage("auth-message", "Logged out", false);
  });
}

async function loadDepartments() {
  try {
    const departments = await api("/api/departments");
    const list = document.getElementById("department-list");
    const select = document.getElementById("emp-department");
    const filterSelect = document.getElementById("emp-search-dept");

    list.innerHTML = "";
    select.innerHTML = "";
    filterSelect.innerHTML =
      '<option value="">All</option>';

    departments.forEach((d) => {
      const li = document.createElement("li");
      li.textContent = `${d.id} – ${d.name}`;
      list.appendChild(li);

      const opt = document.createElement("option");
      opt.value = d.id;
      opt.textContent = d.name;
      select.appendChild(opt);

      const opt2 = document.createElement("option");
      opt2.value = d.id;
      opt2.textContent = d.name;
      filterSelect.appendChild(opt2);
    });
  } catch (err) {
    setMessage("department-message", err.message, true);
  }
}

function setupDepartments() {
  document
    .getElementById("department-form")
    .addEventListener("submit", async (e) => {
      e.preventDefault();
      setMessage("department-message", "");
      try {
        const body = {
          name: document.getElementById("department-name").value.trim(),
        };
        await api("/api/departments", {
          method: "POST",
          body: JSON.stringify(body),
        });
        document.getElementById("department-name").value = "";
        setMessage("department-message", "Department added", false);
        await loadDepartments();
      } catch (err) {
        setMessage("department-message", err.message, true);
      }
    });

  document
    .getElementById("refresh-departments")
    .addEventListener("click", loadDepartments);
}

async function loadEmployees() {
  try {
    const name = document
      .getElementById("emp-search-name")
      .value.trim();
    const deptId = document.getElementById("emp-search-dept").value;
    const params = new URLSearchParams();
    params.set("page", state.page.toString());
    params.set("size", state.size.toString());
    if (name) params.set("name", name);
    if (deptId) params.set("departmentId", deptId);

    const res = await api(`/api/employees?${params.toString()}`);
    state.lastPage = res.totalPages ? res.totalPages - 1 : 0;

    const tbody = document.getElementById("employee-table-body");
    tbody.innerHTML = "";

    res.content.forEach((e) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${e.id}</td>
        <td>${e.name}</td>
        <td>${e.email}</td>
        <td>${e.salary ?? "-"}</td>
        <td>${e.joiningDate ?? "-"}</td>
        <td>${e.departmentName ?? "-"}</td>
      `;
      tbody.appendChild(tr);
    });

    document.getElementById(
      "emp-page-info"
    ).textContent = `Page ${res.number + 1} of ${res.totalPages || 1}`;

    document.getElementById("emp-prev").disabled = state.page <= 0;
    document.getElementById("emp-next").disabled =
      state.page >= state.lastPage;
  } catch (err) {
    setMessage("employee-message", err.message, true);
  }
}

function setupEmployees() {
  document
    .getElementById("employee-form")
    .addEventListener("submit", async (e) => {
      e.preventDefault();
      setMessage("employee-message", "");
      try {
        const body = {
          name: document.getElementById("emp-name").value.trim(),
          email: document.getElementById("emp-email").value.trim(),
          salary: parseFloat(
            document.getElementById("emp-salary").value || "0"
          ),
          joiningDate: document.getElementById("emp-joining-date").value,
          departmentId: parseInt(
            document.getElementById("emp-department").value,
            10
          ),
        };
        await api("/api/employees", {
          method: "POST",
          body: JSON.stringify(body),
        });
        document.getElementById("employee-form").reset();
        setMessage("employee-message", "Employee added", false);
        state.page = 0;
        await loadEmployees();
      } catch (err) {
        setMessage("employee-message", err.message, true);
      }
    });

  document.getElementById("search-employees").addEventListener("click", () => {
    state.page = 0;
    loadEmployees();
  });

  document.getElementById("emp-prev").addEventListener("click", () => {
    if (state.page > 0) {
      state.page -= 1;
      loadEmployees();
    }
  });

  document.getElementById("emp-next").addEventListener("click", () => {
    if (state.page < state.lastPage) {
      state.page += 1;
      loadEmployees();
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  setupTabs();
  setupAuth();
  setupDepartments();
  setupEmployees();
  setAuthStatus();
});

