/**
 * Sunrise Dental Clinic - Common Utilities and UI Components
 */

/**
 * Checks if user is authenticated; if not, redirects to login.html.
 * If authenticated, validates session with backend.
 */
async function requireAuth() {
    const token = API.getToken();
    if (!token) {
        window.location.href = "login.html?error=" + encodeURIComponent("Please login to access this page.");
        return null;
    }

    try {
        const response = await API.get("/api/auth/session");
        if (response && response.success && response.data) {
            API.setSession(token, response.data);
            return response.data;
        } else {
            throw new Error("Invalid session");
        }
    } catch (e) {
        API.clearSession();
        window.location.href = "login.html?error=" + encodeURIComponent("Session expired. Please login again.");
        return null;
    }
}

/**
 * Renders the top navigation bar dynamically across all dashboard and portal pages.
 * @param {string} activePage Key identifying current page
 */
function renderNav(activePage = "") {
    const navPlaceholder = document.getElementById("navbar-placeholder");
    if (!navPlaceholder) return;

    const user = API.getUser() || { firstName: "Staff", lastName: "Member", staffId: "STF" };
    const fullName = (user.firstName ? (user.firstName + " " + (user.lastName || "")) : user.username) || "Staff";

    const links = [
        { id: "dashboard", href: "dashboard.html", label: "Dashboard" },
        { id: "new-appointment", href: "new-appointment.html", label: "New Appointment" },
        { id: "appointments", href: "appointments.html", label: "Appointments" },
        { id: "search", href: "search-appointment.html", label: "Search" },
        { id: "billing", href: "billing.html", label: "Billing" },
        { id: "help", href: "help.html", label: "Help" }
    ];

    const linksHtml = links.map(l => `
        <li>
            <a href="${l.href}" class="${activePage === l.id ? 'active' : ''}">
                ${l.label}
            </a>
        </li>
    `).join("");

    navPlaceholder.innerHTML = `
        <nav class="navbar">
            <div class="nav-container">
                <a href="dashboard.html" class="nav-brand">
                    <svg viewBox="0 0 24 24">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 14h2v2h-2v-2zm0-10h2v8h-2V6z"/>
                    </svg>
                    Sunrise Dental Clinic
                </a>
                <ul class="nav-links">
                    ${linksHtml}
                </ul>
                <div class="nav-user">
                    <div class="user-badge">
                        <span class="user-name">${fullName}</span>
                        <span class="user-role">Staff ID: ${user.staffId || 'STF'}</span>
                    </div>
                    <button class="btn-logout" id="btn-logout-nav" title="Log out of session">Logout</button>
                </div>
            </div>
        </nav>
    `;

    const logoutBtn = document.getElementById("btn-logout-nav");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", logout);
    }
}

/**
 * Handles user logout: informs backend, clears storage, redirects to login.
 */
async function logout() {
    try {
        await API.post("/api/auth/logout", {});
    } catch (e) {
        console.warn("Logout notification error:", e);
    } finally {
        API.clearSession();
        window.location.href = "login.html?loggedOut=true";
    }
}

/**
 * Displays an alert message banner inside a specified container element.
 * @param {string} message Message text
 * @param {'success'|'danger'|'warning'|'info'} type Alert style type
 * @param {string} containerId ID of container DOM element
 */
function showAlert(message, type = "info", containerId = "alert-container") {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = `
        <div class="alert alert-${type}">
            <span>${message}</span>
        </div>
    `;
    container.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

/**
 * Formats a monetary amount into Sri Lankan Rupee currency format: Rs. 1,500.00
 * @param {number|string} amount
 */
function formatCurrency(amount) {
    if (amount === undefined || amount === null) return "Rs. 0.00";
    const num = parseFloat(amount);
    if (isNaN(num)) return "Rs. 0.00";
    return "Rs. " + num.toLocaleString("en-LK", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

/**
 * Formats a date string into readable DD/MM/YYYY
 * @param {string} dateStr YYYY-MM-DD
 */
function formatDate(dateStr) {
    if (!dateStr) return "";
    const parts = dateStr.split("-");
    if (parts.length === 3) {
        return `${parts[2]}/${parts[1]}/${parts[0]}`;
    }
    return dateStr;
}
