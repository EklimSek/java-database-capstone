-- Doctors
INSERT INTO doctor (email, name, password, phone, specialty) VALUES
('dr.adams@example.com', 'Dr. Emily Adams', 'pass12345', '555-101-2020', 'Cardiologist'),
('dr.johnson@example.com', 'Dr. Mark Johnson', 'secure4567', '555-202-3030', 'Neurologist'),
-- ... (all 25 doctor rows you already ran)
('dr.ward@example.com', 'Dr. Ruby Ward', 'wardWard', '555-555-6667', 'Dermatologist');

-- Patients
INSERT INTO patient (address, email, name, password, phone) VALUES
('101 Oak St, Cityville', 'jane.doe@example.com', 'Jane Doe', 'passJane1', '888-111-1111'),
-- ... (all 25 patient rows)
('126 Olive Rd, Ashville', 'ella.moore@example.com', 'Ella Moore', 'ellamoore', '890-555-5555');

-- Appointments
INSERT INTO appointment (appointment_time, status, doctor_id, patient_id) VALUES
('2025-05-01 09:00:00.000000', 0, 1, 1),
-- ... (all 130 appointment rows)
('2025-04-10 13:00:00.000000', 1, 4, 7);

-- Admin
INSERT INTO admin (username, password)
VALUES ('admin', 'admin@1234');

-- Doctor available times (still pending — insert once you've written these)
-- INSERT INTO doctor_available_times (doctor_id, available_times) VALUES ...