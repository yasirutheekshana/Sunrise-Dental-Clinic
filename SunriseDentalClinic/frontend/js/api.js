/**
 * Sunrise Dental Clinic - Centralized API Client
 * Uses pure Vanilla JavaScript and standard Fetch API (No Frameworks).
 */

const API_BASE_URL = window.location.origin;

const API = {
    getToken() {
        return sessionStorage.getItem("dental_session_token");
    },

    setSession(token, user) {
        if (token) {
            sessionStorage.setItem("dental_session_token", token);
        }
        if (user) {
            sessionStorage.setItem("dental_user", JSON.stringify(user));
        }
    },

    getUser() {
        const raw = sessionStorage.getItem("dental_user");
        try {
            return raw ? JSON.parse(raw) : null;
        } catch (e) {
            return null;
        }
    },

    clearSession() {
        sessionStorage.removeItem("dental_session_token");
        sessionStorage.removeItem("dental_user");
    },

    async request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint}`;
        const headers = {
            "Content-Type": "application/json",
            "Accept": "application/json",
            ...(options.headers || {})
        };

        const token = this.getToken();
        if (token) {
            headers["X-Session-Id"] = token;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);
            const data = await response.json().catch(() => ({}));

            if (!response.ok) {
                // If unauthorized and not on login/signup page, redirect
                if (response.status === 401) {
                    this.clearSession();
                    const currentPath = window.location.pathname;
                    if (!currentPath.includes("login.html") && !currentPath.includes("signup.html")) {
                        window.location.href = "login.html?error=" + encodeURIComponent("Session expired. Please log in again.");
                        return;
                    }
                }
                const errorMessage = data && data.message ? data.message : `Request failed with status ${response.status}`;
                const error = new Error(errorMessage);
                error.status = response.status;
                error.data = data;
                throw error;
            }

            return data;
        } catch (err) {
            console.error(`API Error [${options.method || "GET"} ${endpoint}]:`, err);
            throw err;
        }
    },

    get(endpoint) {
        return this.request(endpoint, { method: "GET" });
    },

    post(endpoint, body) {
        return this.request(endpoint, {
            method: "POST",
            body: JSON.stringify(body)
        });
    },

    put(endpoint, body) {
        return this.request(endpoint, {
            method: "PUT",
            body: JSON.stringify(body)
        });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: "DELETE" });
    }
};
