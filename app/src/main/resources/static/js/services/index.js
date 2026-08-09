import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

window.onload = function () {
    const adminLoginBtn = document.getElementById("adminLogin");
    const doctorLoginBtn = document.getElementById("doctorLogin");

    if (adminLoginBtn) {
        adminLoginBtn.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }

    if (doctorLoginBtn) {
        doctorLoginBtn.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }
};

window.adminLoginHandler = async function () {
    try {
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        const admin = { username, password };

        const response = await fetch(ADMIN_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            selectRole("admin");
        } else {
            alert("Invalid admin credentials!");
        }
    } catch (error) {
        console.error("Error during admin login:", error);
        alert("Something went wrong during admin login.");
    }
};

window.doctorLoginHandler = async function () {
    try {
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        const doctor = { identifier: email, password };

        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            selectRole("doctor");
        } else {
            alert("Invalid doctor credentials!");
        }
    } catch (error) {
        console.error("Error during doctor login:", error);
        alert("Something went wrong during doctor login.");
    }
};