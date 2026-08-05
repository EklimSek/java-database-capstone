# User Stories – Smart Clinic Management System

## Admin

### 1. Admin Login
**Title:** Admin Login
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._
**Acceptance Criteria:**
1. Login form accepts username and password
2. Valid credentials grant access to the admin dashboard
3. Invalid credentials show an error message
**Priority:** High
**Story Points:** 3
**Notes:**
- Passwords should be stored hashed, not in plain text

### 2. Admin Logout
**Title:** Admin Logout
_As an admin, I want to log out of the portal, so that I can protect system access._
**Acceptance Criteria:**
1. Logout option is accessible from the admin dashboard
2. Logging out invalidates the current session/token
3. User is redirected to the login page after logout
**Priority:** Medium
**Story Points:** 2
**Notes:**
- None

### 3. Add Doctor
**Title:** Add Doctor
_As an admin, I want to add doctors to the portal, so that patients can book appointments with them._
**Acceptance Criteria:**
1. Admin can enter doctor name, specialty, and contact info
2. New doctor appears in the doctor directory immediately
3. Required fields are validated before submission
**Priority:** High
**Story Points:** 3
**Notes:**
- None

### 4. Delete Doctor Profile
**Title:** Delete Doctor Profile
_As an admin, I want to delete a doctor's profile from the portal, so that I can keep the directory accurate._
**Acceptance Criteria:**
1. Admin can select a doctor and remove their profile
2. System asks for confirmation before deletion
3. Deleting a doctor doesn't erase historical appointment records
**Priority:** Medium
**Story Points:** 3
**Notes:**
- None

### 5. Monthly Appointment Stats
**Title:** Monthly Appointment Stats
_As an admin, I want to run a stored procedure in the MySQL CLI to get the number of appointments per month, so that I can track usage statistics._
**Acceptance Criteria:**
1. A stored procedure exists that returns appointment counts grouped by month
2. Admin can call it directly via the MySQL CLI
3. Output is accurate against the appointments table
**Priority:** Low
**Story Points:** 5
**Notes:**
- Ties into Module 3 (stored procedures)

## Doctor

### 1. Doctor Login
**Title:** Doctor Login
_As a doctor, I want to log into the portal, so that I can manage my appointments._
**Acceptance Criteria:**
1. Login accepts credentials via JWT-based authentication
2. Valid credentials grant access to the doctor dashboard
3. Invalid credentials show an error message
**Priority:** High
**Story Points:** 3
**Notes:**
- None

### 2. Doctor Logout
**Title:** Doctor Logout
_As a doctor, I want to log out of the portal, so that I can protect my data._
**Acceptance Criteria:**
1. Logout option is accessible from the doctor dashboard
2. Logging out invalidates the session/token
3. User is redirected to the login page
**Priority:** Medium
**Story Points:** 2
**Notes:**
- None

### 3. View Appointment Calendar
**Title:** View Appointment Calendar
_As a doctor, I want to view my appointment calendar, so that I can stay organized._
**Acceptance Criteria:**
1. Doctor sees appointments displayed in a calendar or chronological view
2. Calendar reflects real-time booking updates
3. Doctor can click an entry for appointment details
**Priority:** High
**Story Points:** 5
**Notes:**
- None

### 4. Mark Unavailability
**Title:** Mark Unavailability
_As a doctor, I want to mark my unavailability, so that patients only see available slots._
**Acceptance Criteria:**
1. Doctor can block off specific dates/times as unavailable
2. Blocked times are removed from the patient-facing booking view
3. Existing bookings in a newly-blocked slot trigger a conflict warning
**Priority:** High
**Story Points:** 5
**Notes:**
- None

### 5. Update Profile
**Title:** Update Profile
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._
**Acceptance Criteria:**
1. Doctor can edit specialty and contact fields
2. Changes are reflected immediately in the patient-facing doctor directory
3. Required fields are validated before saving
**Priority:** Medium
**Story Points:** 3
**Notes:**
- None

### 6. View Patient Details for Appointments
**Title:** View Patient Details for Appointments
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._
**Acceptance Criteria:**
1. Doctor can open an appointment and see the patient's basic info
2. Doctor can view the patient's relevant appointment/prescription history
3. Access is restricted to appointments the doctor is actually assigned to
**Priority:** Medium
**Story Points:** 5
**Notes:**
- None

## Patient

### 1. Browse Doctors Without Login
**Title:** Browse Doctors Without Login
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._
**Acceptance Criteria:**
1. Doctor list is publicly accessible without authentication
2. List shows name and specialty at minimum
3. Booking still requires login
**Priority:** Medium
**Story Points:** 2
**Notes:**
- None

### 2. Patient Signup
**Title:** Patient Signup
_As a patient, I want to sign up using my email and password, so that I can book appointments._
**Acceptance Criteria:**
1. Signup form requires valid email and password
2. Duplicate email registration is rejected
3. Successful signup logs the patient in or redirects to login
**Priority:** High
**Story Points:** 3
**Notes:**
- None

### 3. Patient Login
**Title:** Patient Login
_As a patient, I want to log into the portal, so that I can manage my bookings._
**Acceptance Criteria:**
1. Login accepts email and password
2. Valid credentials grant access to the patient dashboard
3. Invalid credentials show an error message
**Priority:** High
**Story Points:** 3
**Notes:**
- None

### 4. Patient Logout
**Title:** Patient Logout
_As a patient, I want to log out of the portal, so that I can secure my account._
**Acceptance Criteria:**
1. Logout option is accessible from the patient dashboard
2. Logging out invalidates the session/token
3. User is redirected to the login page
**Priority:** Medium
**Story Points:** 2
**Notes:**
- None

### 5. Book Hour-Long Appointment
**Title:** Book Hour-Long Appointment
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._
**Acceptance Criteria:**
1. Patient can select a doctor and an available hour-long slot
2. Booking confirms and removes that slot from availability
3. Patient receives confirmation of the booked appointment
**Priority:** High
**Story Points:** 8
**Notes:**
- Needs concurrency handling to avoid double-booking

### 6. View Upcoming Appointments
**Title:** View Upcoming Appointments
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._
**Acceptance Criteria:**
1. Patient sees a list of upcoming appointments with date, time, and doctor
2. List updates after a new booking or cancellation
3. Patient can click into an appointment for details
**Priority:** Medium
**Story Points:** 3
**Notes:**
- None
