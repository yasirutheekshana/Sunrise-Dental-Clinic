/**
 * Sunrise Dental Clinic - Search Appointment Logic
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = await requireAuth();
    if (!user) return;

    renderNav("search");
    initSearchPage();
});

function initSearchPage() {
    const form = document.getElementById("search-form");
    const input = document.getElementById("appointment-number-input");
    const resultCard = document.getElementById("search-result-card");
    const btnSearch = document.getElementById("btn-search-submit");

    // Check if query param exists: ?apt=APT-XXXX-XXXX
    const params = new URLSearchParams(window.location.search);
    const prefill = params.get("apt");
    if (prefill) {
        input.value = prefill;
        performSearch(prefill);
    }

    form.addEventListener("submit", (e) => {
        e.preventDefault();
        const aptNum = input.value.trim();
        if (!aptNum) {
            showAlert("Please enter an appointment number.", "warning");
            return;
        }
        performSearch(aptNum);
    });

    async function performSearch(aptNumber) {
        try {
            btnSearch.disabled = true;
            btnSearch.textContent = "Searching...";
            resultCard.style.display = "none";

            const res = await API.get(`/api/appointments/${encodeURIComponent(aptNumber)}`);

            if (res.success && res.data) {
                renderResult(res.data);
            } else {
                showAlert("No appointment found. Please check the appointment number.", "danger");
            }
        } catch (err) {
            showAlert("No appointment found. Please check the appointment number.", "danger");
        } finally {
            btnSearch.disabled = false;
            btnSearch.textContent = "Search";
        }
    }

    function renderResult(apt) {
        document.getElementById("res-apt-number").textContent = apt.appointmentNumber;
        document.getElementById("res-patient-name").textContent = apt.patientName;
        document.getElementById("res-address").textContent = apt.address;
        document.getElementById("res-contact").textContent = apt.contactNumber;
        document.getElementById("res-dentist").textContent = apt.dentistName;
        document.getElementById("res-treatment").textContent = apt.treatmentType;
        document.getElementById("res-date").textContent = formatDate(apt.appointmentDate);
        document.getElementById("res-time").textContent = apt.appointmentTime;

        const badgeEl = document.getElementById("res-status-badge");
        badgeEl.textContent = apt.status;
        badgeEl.className = `badge badge-${apt.status.toLowerCase()}`;

        // Link to billing
        const billLink = document.getElementById("btn-go-billing");
        if (billLink) {
            billLink.href = `billing.html?apt=${encodeURIComponent(apt.appointmentNumber)}`;
        }

        resultCard.style.display = "block";
        resultCard.scrollIntoView({ behavior: "smooth" });
    }
}
