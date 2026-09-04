/**
 * Sunrise Dental Clinic - Billing and Receipt Logic
 */

document.addEventListener("DOMContentLoaded", async () => {
    const user = await requireAuth();
    if (!user) return;

    renderNav("billing");
    initBillingPage();
});

let currentAppointment = null;
let defaultConsultationFee = 1500.00;

async function initBillingPage() {
    const searchForm = document.getElementById("billing-search-form");
    const aptInput = document.getElementById("billing-apt-input");
    const billBreakdownCard = document.getElementById("billing-breakdown-card");
    const receiptContainer = document.getElementById("receipt-container");
    const btnGenerateBill = document.getElementById("btn-generate-bill");
    const btnPrintBill = document.getElementById("btn-print-bill");

    // Fetch default consultation fee
    try {
        const feeRes = await API.get("/api/bills/fee");
        if (feeRes.success && feeRes.data) {
            defaultConsultationFee = parseFloat(feeRes.data.consultationFee) || 1500.00;
        }
    } catch (e) {
        console.warn("Could not fetch consultation fee:", e);
    }

    // Check if query param exists: ?apt=APT-XXXX-XXXX
    const params = new URLSearchParams(window.location.search);
    const prefillApt = params.get("apt");
    if (prefillApt) {
        aptInput.value = prefillApt;
        lookupAppointmentForBilling(prefillApt);
    }

    searchForm.addEventListener("submit", (e) => {
        e.preventDefault();
        const aptNum = aptInput.value.trim();
        if (!aptNum) {
            showAlert("Please enter an appointment number.", "warning");
            return;
        }
        lookupAppointmentForBilling(aptNum);
    });

    async function lookupAppointmentForBilling(aptNumber) {
        try {
            billBreakdownCard.style.display = "none";
            receiptContainer.style.display = "none";

            // 1. Fetch appointment details
            const aptRes = await API.get(`/api/appointments/${encodeURIComponent(aptNumber)}`);
            if (!aptRes.success || !aptRes.data) {
                showAlert("Appointment not found. Please verify the appointment number.", "danger");
                return;
            }
            currentAppointment = aptRes.data;

            // 2. Fetch treatment cost
            const trtRes = await API.get("/api/treatments");
            let treatmentCost = 3000.00;
            if (trtRes.success && trtRes.data) {
                const trt = trtRes.data.find(t => t.treatmentName.toLowerCase() === currentAppointment.treatmentType.toLowerCase());
                if (trt) {
                    treatmentCost = parseFloat(trt.treatmentCost);
                }
            }

            // 3. Check if bill is already generated
            try {
                const billRes = await API.get(`/api/bills/${encodeURIComponent(aptNumber)}`);
                if (billRes.success && billRes.data) {
                    renderReceipt(billRes.data);
                    return;
                }
            } catch (e) {
                // Bill not generated yet, show preview breakdown
            }

            // Render Preview Breakdown
            renderBreakdown(currentAppointment, treatmentCost, defaultConsultationFee);

        } catch (err) {
            showAlert("Appointment not found. Please verify the appointment number.", "danger");
        }
    }

    function renderBreakdown(apt, treatmentCost, consultationFee) {
        const total = treatmentCost + consultationFee;

        document.getElementById("calc-apt-number").textContent = apt.appointmentNumber;
        document.getElementById("calc-patient-name").textContent = apt.patientName;
        document.getElementById("calc-dentist").textContent = apt.dentistName;
        document.getElementById("calc-treatment").textContent = apt.treatmentType;
        document.getElementById("calc-treatment-cost").textContent = formatCurrency(treatmentCost);
        document.getElementById("calc-consultation-fee").textContent = formatCurrency(consultationFee);
        document.getElementById("calc-total").textContent = formatCurrency(total);

        billBreakdownCard.style.display = "block";
        billBreakdownCard.scrollIntoView({ behavior: "smooth" });
    }

    btnGenerateBill.addEventListener("click", async () => {
        if (!currentAppointment) return;

        try {
            btnGenerateBill.disabled = true;
            btnGenerateBill.textContent = "Processing Bill...";

            const res = await API.post("/api/bills", {
                appointmentNumber: currentAppointment.appointmentNumber
            });

            if (res.success && res.data) {
                showAlert("Bill generated and saved successfully!", "success");
                billBreakdownCard.style.display = "none";
                renderReceipt(res.data);
            } else {
                showAlert("Unable to calculate bill.", "danger");
            }
        } catch (err) {
            showAlert(err.message || "Unable to calculate bill.", "danger");
        } finally {
            btnGenerateBill.disabled = false;
            btnGenerateBill.textContent = "Confirm & Generate Bill";
        }
    });

    function renderReceipt(bill) {
        document.getElementById("rec-bill-id").textContent = bill.billId;
        document.getElementById("rec-apt-number").textContent = bill.appointmentNumber;
        document.getElementById("rec-date").textContent = new Date(bill.generatedAt || Date.now()).toLocaleDateString("en-GB");
        document.getElementById("rec-patient").textContent = bill.patientName;
        document.getElementById("rec-dentist").textContent = bill.dentistName;
        document.getElementById("rec-treatment").textContent = bill.treatmentType;
        document.getElementById("rec-staff").textContent = bill.generatedBy || "Staff";

        document.getElementById("rec-treatment-cost").textContent = formatCurrency(bill.treatmentCost);
        document.getElementById("rec-consultation-fee").textContent = formatCurrency(bill.consultationFee);
        document.getElementById("rec-total").textContent = formatCurrency(bill.total);

        receiptContainer.style.display = "block";
        receiptContainer.scrollIntoView({ behavior: "smooth" });
    }

    btnPrintBill.addEventListener("click", () => {
        window.print();
    });
}
