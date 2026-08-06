## MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Not Null, Unique
- phone: VARCHAR(20), Not Null
- password_hash: VARCHAR(255), Not Null
- date_of_birth: DATE
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Not Null, Unique
- phone: VARCHAR(20), Not Null
- password_hash: VARCHAR(255), Not Null
- specialty: VARCHAR(100), Not Null
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Not Null, Unique
- password_hash: VARCHAR(255), Not Null
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- duration_minutes: INT, Not Null, Default 60
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- created_at: DATETIME, Not Null, Default CURRENT_TIMESTAMP

### Table: doctor_availability
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- day_of_week: TINYINT (0 = Sunday ... 6 = Saturday)
- start_time: TIME, Not Null
- end_time: TIME, Not Null

### Table: clinic_locations
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- address: VARCHAR(255), Not Null
- phone: VARCHAR(20)

### Table: payments
- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key → appointments(id)
- amount: DECIMAL(10,2), Not Null
- status: INT (0 = Pending, 1 = Paid, 2 = Refunded)
- paid_at: DATETIME

**Design decisions:**
- `appointments.status` uses an integer enum rather than a string to keep the column small and indexable; the mapping is documented here and enforced in application code.
- Deleting a patient does **not** cascade-delete their appointments — instead, patient deletion should be a soft delete (add a `deleted_at` column) so appointment history and clinic records stay intact for reporting and compliance.
- Doctor availability is modeled as recurring weekly slots (`doctor_availability`) rather than one row per date, keeping the table small; one-off exceptions (e.g. a doctor's day off) could later be handled with a separate `doctor_time_off` table if needed.
- Overlapping appointments for the same doctor are prevented at the application/service layer (checked against `doctor_availability` and existing `appointments`) rather than a DB constraint, since MySQL doesn't support range-overlap constraints natively.
- `payments` is kept as a separate table (not embedded in `appointments`) since one appointment could conceivably have multiple payment attempts or a refund record.

## MongoDB Collection Design

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "instructions": "Take 1 tablet every 6 hours.",
  "doctorNotes": "Monitor for fever after 48 hours.",
  "refillCount": 2,
  "issuedAt": "2026-08-01T10:15:00Z",
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  },
  "tags": ["pain-relief", "short-term"]
}
```

### Collection: feedback
```json
{
  "_id": "ObjectId('64def789012')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "rating": 5,
  "comment": "Doctor was thorough and explained everything clearly.",
  "submittedAt": "2026-08-02T14:30:00Z"
}
```

**Design decisions:**
- Documents store `appointmentId`, `patientId`, and `doctorId` as references (not embedded objects), keeping MongoDB documents lightweight and avoiding data duplication that would go stale if a patient's name changes.
- A chat message document would follow a similar reference pattern:
```json
  {
    "_id": "ObjectId('...')",
    "conversationId": "doctor7-patient12",
    "senderId": 12,
    "senderRole": "patient",
    "message": "Can I reschedule to Thursday?",
    "sentAt": "2026-08-03T09:00:00Z"
  }
```
- Schema evolution is supported naturally by MongoDB's flexible structure — e.g., adding a `metadata` or `attachments` array to `prescriptions` later wouldn't require a migration, just optional handling in the application code for older documents that lack the field.
- `tags` is included as an array to allow flexible categorization (e.g., filtering all "short-term" prescriptions) without needing a separate join table like MySQL would require.