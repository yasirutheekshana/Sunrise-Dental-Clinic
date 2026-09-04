/**
 * Sunrise Dental Clinic - Authentication Logic (Login & Signup)
 */

document.addEventListener("DOMContentLoaded", () => {
    // Check if on login page
    const loginForm = document.getElementById("login-form");
    if (loginForm) {
        initLoginPage();
    }

    // Check if on signup page
    const signupForm = document.getElementById("signup-form");
    if (signupForm) {
        initSignupPage();
    }
});

function initLoginPage() {
    const loginForm = document.getElementById("login-form");
    const submitBtn = document.getElementById("btn-login-submit");

    // Check URL parameters for banners
    const params = new URLSearchParams(window.location.search);
    if (params.get("loggedOut")) {
        showAlert("You have been logged out successfully.", "success");
    } else if (params.get("signupSuccess")) {
        showAlert("Staff account created successfully. Please login to continue.", "success");
    } else if (params.get("error")) {
        showAlert(params.get("error"), "warning");
    }

    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;

        if (!username) {
            showAlert("Please enter your username.", "danger");
            document.getElementById("username").focus();
            return;
        }

        if (!password) {
            showAlert("Please enter your password.", "danger");
            document.getElementById("password").focus();
            return;
        }

        try {
            submitBtn.disabled = true;
            submitBtn.textContent = "Authenticating...";

            const response = await API.post("/api/auth/login", {
                username,
                password
            });

            if (response.success && response.data) {
                API.setSession(response.data.token, response.data);
                window.location.href = "dashboard.html";
            } else {
                showAlert(response.message || "Invalid username or password.", "danger");
            }
        } catch (err) {
            showAlert(err.message || "Invalid username or password.", "danger");
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = "Login";
        }
    });
}

function initSignupPage() {
    const signupForm = document.getElementById("signup-form");
    const submitBtn = document.getElementById("btn-signup-submit");

    signupForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const username = document.getElementById("username").value.trim();
        const firstName = document.getElementById("firstName").value.trim();
        const lastName = document.getElementById("lastName").value.trim();
        const staffId = document.getElementById("staffId").value.trim();
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        // Validations
        if (!username) {
            showAlert("Please enter your username.", "danger");
            return;
        }
        if (!firstName) {
            showAlert("Please enter your first name.", "danger");
            return;
        }
        if (!lastName) {
            showAlert("Please enter your last name.", "danger");
            return;
        }
        if (!staffId) {
            showAlert("Please enter your Staff ID.", "danger");
            return;
        }
        if (!password) {
            showAlert("Please enter your password.", "danger");
            return;
        }
        if (password.length < 6) {
            showAlert("Password must be at least 6 characters long.", "danger");
            return;
        }
        if (password !== confirmPassword) {
            showAlert("Passwords do not match.", "danger");
            return;
        }

        try {
            submitBtn.disabled = true;
            submitBtn.textContent = "Creating Account...";

            const response = await API.post("/api/auth/signup", {
                username,
                firstName,
                lastName,
                staffId,
                password,
                confirmPassword
            });

            if (response.success) {
                window.location.href = "login.html?signupSuccess=true";
            } else {
                showAlert(response.message || "Unable to register staff member.", "danger");
            }
        } catch (err) {
            showAlert(err.message || "Unable to register staff member.", "danger");
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = "Register";
        }
    });
}
