import { getDoctors, filterDoctors, saveDoctor } from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";
import { openModal } from "../components/modals.js";

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            openModal("addDoctor");
        });
    }

    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }
    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }
    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";

        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });
    } catch (error) {
        console.error("Error loading doctor cards:", error);
    }
}

async function filterDoctorsOnChange() {
    try {
        const searchBar = document.getElementById("searchBar");
        const filterTime = document.getElementById("filterTime");
        const filterSpecialty = document.getElementById("filterSpecialty");

        const name = searchBar.value.trim() || null;
        const time = filterTime.value || null;
        const specialty = filterSpecialty.value || null;

        const result = await filterDoctors(name, time, specialty);

        if (result.doctors && result.doctors.length > 0) {
            renderDoctorCards(result.doctors);
        } else {
            const contentDiv = document.getElementById("content");
            contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found with the given filters.</p>";
        }
    } catch (error) {
        alert("An error occurred while filtering doctors.");
        console.error("Error filtering doctors:", error);
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

window.adminAddDoctor = async function () {
    const name = document.getElementById("doctorName").value;
    const email = document.getElementById("doctorEmail").value;
    const phone = document.getElementById("doctorPhone").value;
    const password = document.getElementById("doctorPassword").value;
    const specialty = document.getElementById("doctorSpecialty").value;
    const availableTimesInput = document.getElementById("doctorAvailableTimes").value;
    const availableTimes = availableTimesInput.split(",").map(t => t.trim());

    const token = localStorage.getItem("token");
    if (!token) {
        alert("You must be logged in as admin to add a doctor.");
        return;
    }

    const doctor = {
        name,
        email,
        phone,
        password,
        specialty,
        availableTimes
    };

    try {
        const result = await saveDoctor(doctor, token);

        if (result.success) {
            alert("Doctor added successfully!");
            document.getElementById("modal").style.display = "none";
            window.location.reload();
        } else {
            alert("Failed to add doctor: " + result.message);
        }
    } catch (error) {
        console.error("Error adding doctor:", error);
        alert("An error occurred while adding the doctor.");
    }
};