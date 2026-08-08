function renderHeader() {
    const headerDiv = document.getElementById("header");

    // Don't show role-based header on the homepage
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = `
            <header class="header">
                <h1 class="logo">Smart Clinic</h1>
            </header>`;
        return;
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Invalid session check
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    let headerContent = `
        <header class="header">
            <h1 class="logo">Smart Clinic</h1>
            <nav>`;

    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
            <a href="#" id="logoutBtn">Logout</a>`;
    } else if (role === "doctor") {
        headerContent += `
            <a href="/doctorDashboard">Home</a>
            <a href="#" id="logoutBtn">Logout</a>`;
    } else if (role === "patient") {
        headerContent += `
            <a href="#" id="loginBtn">Login</a>
            <a href="#" id="signupBtn">Sign Up</a>`;
    } else if (role === "loggedPatient") {
        headerContent += `
            <a href="/pages/patientDashboard.html">Home</a>
            <a href="#" id="appointmentsBtn">Appointments</a>
            <a href="#" id="logoutPatientBtn">Logout</a>`;
    }

    headerContent += `
            </nav>
        </header>`;

    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", logout);
    }

    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", logoutPatient);
    }

    const loginBtn = document.getElementById("loginBtn");
    if (loginBtn) {
        loginBtn.addEventListener("click", () => openModal("patientLogin"));
    }

    const signupBtn = document.getElementById("signupBtn");
    if (signupBtn) {
        signupBtn.addEventListener("click", () => openModal("patientSignup"));
    }

    const appointmentsBtn = document.getElementById("appointmentsBtn");
    if (appointmentsBtn) {
        appointmentsBtn.addEventListener("click", () => {
            window.location.href = "/pages/patientAppointments.html";
        });
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

renderHeader();