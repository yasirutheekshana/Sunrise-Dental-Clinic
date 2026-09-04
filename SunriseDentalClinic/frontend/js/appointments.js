/**
 * Sunrise Dental Clinic - Appointment Management Logic
 * Handles both appointment listing (appointments.html) and registration (new-appointment.html).
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = await requireAuth();
    if (!user) return;

    // Check if on new-appointment page
    if (document.getElementById("new-appointment-form")) {
        renderNav("new-appointment");
        initNewAppointmentPage();
    }

    // Check if on appointments list page
    if (document.getElementById("appointments-table")) {
        renderNav("appointments");
        initAppointmentsListPage();
    }
});

/* ==========================================================================
   New Appointment Registration Logic
   ========================================================================== */
async function initNewAppointmentPage() {
    const form = document.getElementById("new-appointment-form");
    const dateInput = document.getElementById("appointmentDate");
    const treatmentSelect = document.getElementById("treatmentType");
    const costPreview = document.getElementById("treatmentCostPreview");
    const nextNumberPreview = document.getElementById("appointmentNumberPreview");
    const submitBtn = document.getElementById("btn-submit-appointment");

    // Set min date to today
    const today = new Date().toISOString().split("T")[0];
    dateInput.min = today;
    dateInput.value = today;

    // 1. Fetch next sequential number preview
    try {
        const nextRes = await API.get("/api/appointments/next-number");
        if (nextRes.success && nextRes.data) {
            nextNumberPreview.value = nextRes.data.nextAppointmentNumber;
        }
    } catch (e) {
        console.warn("Could not fetch next number preview:", e);
    }

    // 2. Fetch available treatments
    try {
        const trtRes = await API.get("/api/treatments");
        if (trtRes.success && trtRes.data) {
            treatmentSelect.innerHTML = '<option value="">-- Select Treatment --</option>' +
                trtRes.data.map(t => `
                    <option value="${t.treatmentName}" data-cost="${t.treatmentCost}">
                        ${t.treatmentName} (${formatCurrency(t.treatmentCost)})
                    </option>
                `).join("");
        }
    } catch (e) {
        console.error("Failed to load treatments:", e);
    }

    // Update cost preview on treatment selection
    treatmentSelect.addEventListener("change", () => {
        const selected = treatmentSelect.options[treatmentSelect.selectedIndex];
        const cost = selected.getAttribute("data-cost");
        if (cost) {
            costPreview.textContent = "Estimated Cost: " + formatCurrency(cost);
        } else {
            costPreview.textContent = "";
        }
    });

    // 3. Handle Form Submission
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const patientName = document.getElementById("patientName").value.trim();
        const address = document.getElementById("address").value.trim();
        const contactNumber = document.getElementById("contactNumber").value.trim();
        const dentistName = document.getElementById("dentistName").value.trim();
        const treatmentType = treatmentSelect.value;
        const appointmentDate = dateInput.value;
        const appointmentTime = document.getElementById("appointmentTime").value;

        // Client-side validations
        if (!patientName) {
            showAlert("Please enter the patient's name.", "danger");
            return;
        }
        if (!address) {
            showAlert("Please enter the patient's address.", "danger");
            return;
        }
        if (!contactNumber) {
            showAlert("Please enter a valid contact number.", "danger");
            return;
        }
        if (!dentistName) {
            showAlert("Please select a dentist.", "danger");
            return;
        }
        if (!treatmentType) {
            showAlert("Please select a treatment.", "danger");
            return;
        }
        if (!appointmentDate) {
            showAlert("Please select an appointment date.", "danger");
            return;
        }
        if (!appointmentTime) {
            showAlert("Please select an appointment time.", "danger");
            return;
        }

        try {
            submitBtn.disabled = true;
            submitBtn.textContent = "Booking Appointment...";

            const response = await API.post("/api/appointments", {
                patientName,
                address,
                contactNumber,
                dentistName,
                treatmentType,
                appointmentDate,
                appointmentTime
            });

            if (response.success && response.data) {
                showAlert(
                    `Appointment registered successfully! Assigned Number: <strong>${response.data.appointmentNumber}</strong>`,
                    "success"
                );
                form.reset();
                dateInput.value = today;
                costPreview.textContent = "";

                // Refresh preview number for next booking
                const nextRes = await API.get("/api/appointments/next-number");
                if (nextRes.success && nextRes.data) {
                    nextNumberPreview.value = nextRes.data.nextAppointmentNumber;
                }
            } else {
                showAlert(response.message || "Failed to register appointment.", "danger");
            }
        } catch (err) {
            // Handle double-booking conflicts and validation errors
            showAlert(err.message || "Unable to save appointment.", "danger");
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = "Register Appointment";
        }
    });
}

/* ==========================================================================
   Appointments List & Filter Logic
   ========================================================================== */
let allAppointmentsCache = [];

async function initAppointmentsListPage() {
    const searchInput = document.getElementById("search-input");
    const statusFilter = document.getElementById("status-filter");
    const searchBtn = document.getElementById("btn-search");

    async function loadAppointments() {
        try {
            const query = searchInput.value.trim();
            const status = statusFilter.value;

            let endpoint = `/api/appointments?search=${encodeURIComponent(query)}&status=${encodeURIComponent(status)}`;
            const res = await API.get(endpoint);

            if (res.success && res.data) {
                allAppointmentsCache = res.data;
                renderAppointmentsTable(allAppointmentsCache);
            }
        } catch (err) {
            showAlert("Failed to load appointments: " + err.message, "danger");
        }
    }

    searchBtn.addEventListener("click", loadAppointments);
    searchInput.addEventListener("keyup", (e) => {
        if (e.key === "Enter") loadAppointments();
    });
    statusFilter.addEventListener("change", loadAppointments);

    // Initial load
    loadAppointments();
}

function renderAppointmentsTable(appointments) {
    const tbody = document.getElementById("appointments-tbody");
    if (!tbody) return;

    if (!appointments || appointments.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 2.5rem;">
                    No appointments found matching your filter criteria.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = appointments.map(a => {
        const isScheduled = a.status === "SCHEDULED";
        return `
            <tr>
                <td><strong>${a.appointmentNumber}</strong></td>
                <td>
                    <div style="font-weight: 600;">${a.patientName}</div>
                    <div style="font-size: 0.8rem; color: var(--text-muted);">${a.contactNumber}</div>
                </td>
                <td>${a.dentistName}</td>
                <td>${a.treatmentType}</td>
                <td>${formatDate(a.appointmentDate)}</td>
                <td>${a.appointmentTime}</td>
                <td><span class="badge badge-${a.status.toLowerCase()}">${a.status}</span></td>
                <td>
                    <div style="display: flex; gap: 0.35rem; flex-wrap: wrap;">
                        ${isScheduled ? `
                            <button class="btn btn-sm btn-secondary" onclick="updateAppointmentStatus('${a.appointmentNumber}', 'COMPLETED')" title="Mark as Completed">
                                Complete
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="updateAppointmentStatus('${a.appointmentNumber}', 'CANCELLED')" title="Cancel Appointment">
                                Cancel
                            </button>
                        ` : ''}
                        <a href="billing.html?apt=${encodeURIComponent(a.appointmentNumber)}" class="btn btn-sm btn-primary" title="Generate Bill / Receipt">
                            Bill
                        </a>
                    </div>
                </td>
            </tr>
        `;
    }).join("");
}

async function updateAppointmentStatus(appointmentNumber, newStatus) {
    if (!confirm(`Are you sure you want to mark appointment ${appointmentNumber} as ${newStatus}?`)) {
        return;
    }

    try {
        const res = await API.put(`/api/appointments/${encodeURIComponent(appointmentNumber)}/status`, {
            status: newStatus
        });
        if (res.success) {
            showAlert(`Appointment ${appointmentNumber} marked as ${newStatus}.`, "success");
            // Refresh table
            const searchInput = document.getElementById("search-input");
            const statusFilter = document.getElementById("status-filter");
            const resUpdated = await API.get(`/api/appointments?search=${encodeURIComponent(searchInput.value.trim())}&status=${encodeURIComponent(statusFilter.value)}`);
            if (resUpdated.success) {
                renderAppointmentsTable(resUpdated.data);
            }
        }
    } catch (err) {
        showAlert("Failed to update appointment: " + err.message, "danger");
    }
}
