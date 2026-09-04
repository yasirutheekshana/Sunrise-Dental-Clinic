/**
 * Sunrise Dental Clinic - Dashboard Logic
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = await requireAuth();
    if (!user) return;

    renderNav("dashboard");
    loadDashboardData();
});

async function loadDashboardData() {
    try {
        const statsRes = await API.get("/api/dashboard/stats");
        if (statsRes.success && statsRes.data) {
            const stats = statsRes.data;
            document.getElementById("stat-today").textContent = stats.todayAppointments;
            document.getElementById("stat-total").textContent = stats.totalAppointments;
            document.getElementById("stat-scheduled").textContent = stats.scheduledAppointments;
            document.getElementById("stat-completed").textContent = stats.completedAppointments;

            const user = API.getUser();
            const staffName = user ? (user.firstName || user.username) : "Staff";
            document.getElementById("hero-staff-name").textContent = staffName;

            if (stats.currentDate) {
                document.getElementById("current-date-text").textContent = formatDate(stats.currentDate);
            }
        }

        // Fetch Today's schedule
        const appointmentsRes = await API.get("/api/appointments");
        if (appointmentsRes.success && appointmentsRes.data) {
            const todayStr = new Date().toISOString().split("T")[0];
            const todayAppointments = appointmentsRes.data.filter(a => a.appointmentDate === todayStr);
            renderTodayTable(todayAppointments);
        }
    } catch (err) {
        console.error("Failed to load dashboard metrics:", err);
        showAlert("Failed to load live statistics: " + err.message, "danger");
    }
}

function renderTodayTable(appointments) {
    const tbody = document.getElementById("today-appointments-tbody");
    if (!tbody) return;

    if (!appointments || appointments.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 2rem;">
                    No appointments scheduled for today.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = appointments.map(apt => `
        <tr>
            <td><strong>${apt.appointmentNumber}</strong></td>
            <td>${apt.patientName}</td>
            <td>${apt.dentistName}</td>
            <td>${apt.treatmentType}</td>
            <td>${apt.appointmentTime}</td>
            <td>
                <span class="badge badge-${apt.status.toLowerCase()}">${apt.status}</span>
            </td>
        </tr>
    `).join("");
}
