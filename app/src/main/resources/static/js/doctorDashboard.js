import { getAllAppointments } from "../services/appointmentServices.js";
import { createPatientRow } from "../components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0];
const token = localStorage.getItem("token");
let patientName = null;

const searchBar = document.getElementById("searchBar");
if (searchBar) {
    searchBar.addEventListener("input", () => {
        const value = searchBar.value.trim();
        patientName = value !== "" ? value : "null";
        loadAppointments();
    });
}

const todayBtn = document.getElementById("todayAppointmentsBtn");
if (todayBtn) {
    todayBtn.addEventListener("click", () => {
        selectedDate = new Date().toISOString().split("T")[0];
        const datePicker = document.getElementById("filterDate");
        if (datePicker) {
            datePicker.value = selectedDate;
        }
        loadAppointments();
    });
}

const datePicker = document.getElementById("filterDate");
if (datePicker) {
    datePicker.addEventListener("change", () => {
        selectedDate = datePicker.value;
        loadAppointments();
    });
}

async function loadAppointments() {
    try {
        const appointments = await getAllAppointments(selectedDate, patientName, token);
        tableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">No Appointments found for today.</td></tr>`;
            return;
        }

        appointments.forEach(appointment => {
            const patient = {
                id: appointment.patientId,
                name: appointment.patientName,
                phone: appointment.patientPhone,
                email: appointment.patientEmail
            };

            const row = createPatientRow(patient, appointment);
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error("Error loading appointments:", error);
        tableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">Error loading appointments. Try again later.</td></tr>`;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    renderContent();
    loadAppointments();
});