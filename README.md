# Smart Clinic Management System

A full-stack clinic management system built with Spring Boot, supporting three
distinct user roles — Admin, Doctor, and Patient — for scheduling appointments,
managing prescriptions, and running a clinic's day-to-day operations. Built as a
database-design capstone project, with a deliberate dual-database architecture:
MySQL for structured relational data and MongoDB for flexible document storage.

## Features

**Admin**
- Register and manage doctor accounts
- Dashboard view for clinic oversight

**Doctor**
- Login with JWT-based authentication
- View and manage appointment availability by date
- Update or cancel appointments
- Add prescriptions for patients
- Filterable doctor directory (by name, availability, specialty)

**Patient**
- Register and log in
- Book, update, and cancel appointments
- View appointment history and prescriptions
- Filter appointments by condition or doctor name

**Core**
- Role-based JWT authentication across all three user types
- Server-rendered dashboards (Thymeleaf) for Admin and Doctor views
- REST API layer for Patient and Appointment operations
- Bean validation on all incoming requests (e.g. appointment times must be in
  the future, prescription fields have length constraints)

## Why Two Databases

This project intentionally uses two different databases for two different kinds
of data, rather than forcing everything into one:

- **MySQL (via Spring Data JPA)** — stores structured, relational data with
  clear foreign-key relationships: `Doctor`, `Patient`, `Admin`, and
  `Appointment`. These entities have a fixed shape and benefit from relational
  integrity (an appointment always references a real doctor and patient).
- **MongoDB (via Spring Data MongoDB)** — stores `Prescription` documents.
  Prescriptions are more free-form (variable-length doctor notes, potential for
  future fields like multiple medications per prescription) and don't need the
  same relational guarantees, making a flexible document store a better fit.

This split reflects a real architectural decision made during the database
design phase of the project, documented further in `schema-design.md` and
`schema-architecture.md`.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.4, Spring Data JPA, Spring Data MongoDB,
  Spring Validation
- **Databases**: MySQL (relational), MongoDB (document store)
- **Auth**: JWT (jjwt)
- **Frontend**: Thymeleaf (server-rendered Admin/Doctor dashboards), static
  HTML/CSS/JS (Patient-facing pages)
- **Build**: Maven
- **Containerization**: Docker (multi-stage build)

## Project Structure

```
app/
├── src/main/java/com/project/back_end/
│   ├── controllers/    # REST + MVC endpoints (Admin, Doctor, Patient, Appointment, Prescription)
│   ├── services/       # Business logic, including JWT TokenService
│   ├── models/         # JPA entities (MySQL) + MongoDB documents
│   ├── repo/            # Spring Data repositories
│   ├── DTO/             # Data transfer objects
│   ├── config/          # Web configuration
│   └── mvc/             # Dashboard controllers (Thymeleaf views)
├── src/main/resources/
│   ├── templates/        # Thymeleaf templates (admin/, doctor/)
│   ├── static/pages/      # Patient-facing static pages
│   └── application.properties
└── Dockerfile
schema-design.md            # Database schema documentation
schema-architecture.md      # Architecture decisions
sample-data.sql              # Sample MySQL data
stored_procedures.sql        # MySQL stored procedures
user_stories.md               # Project requirements as user stories
```

## Getting Started

### Prerequisites

- Java 17
- Maven (or use the included `mvnw` wrapper)
- MySQL running locally (or accessible instance)
- MongoDB running locally (or accessible instance)

### Configuration

Before running, update `app/src/main/resources/application.properties` with
your own database credentials and a private JWT secret — **do not use the
committed placeholder values in any real deployment.** See
[Known Limitations](#known-limitations--roadmap) below.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cms?usessl=false
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

spring.data.mongodb.uri=mongodb://localhost:27017/cms

jwt.secret=your_own_long_random_secret
```

### Running locally

```bash
cd app
./mvnw spring-boot:run
```

The app runs on `http://localhost:8080` by default.

### Running with Docker

```bash
cd app
docker build -t smart-clinic .
docker run -p 8080:8080 smart-clinic
```

Note: the container runs the Spring Boot app itself — you'll still need MySQL
and MongoDB reachable from the container (either running alongside via Docker
Compose, or pointed at external instances via environment-configured
properties).

### Sample data

`sample-data.sql` and `stored_procedures.sql` at the project root can be run
against your MySQL instance to seed sample doctors, patients, and appointments
for testing.

## API Overview

**Admin** — `/admin`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/admin` | Register a new admin |

**Doctor** — `/doctor`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/doctor` | List all doctors |
| GET | `/doctor/availability/{user}/{doctorId}/{date}/{token}` | Check availability |
| GET | `/doctor/filter/{name}/{time}/{speciality}` | Filter doctors |
| POST | `/doctor/login` | Doctor login |
| POST | `/doctor/{token}` | Register a doctor (admin action) |
| PUT | `/doctor/{token}` | Update doctor info |
| DELETE | `/doctor/{id}/{token}` | Remove a doctor |

**Patient** — `/patient`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/patient` | Register a new patient |
| POST | `/patient/login` | Patient login |
| GET | `/patient/{token}` | Get patient profile |
| GET | `/patient/{id}/{token}` | Get patient by ID |
| GET | `/patient/filter/{condition}/{name}/{token}` | Filter patient records |

**Appointment** — `/appointments`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/appointments/{date}/{patientName}/{token}` | List appointments |
| POST | `/appointments/{token}` | Book an appointment |
| PUT | `/appointments/{token}` | Update an appointment |
| DELETE | `/appointments/{id}/{token}` | Cancel an appointment |

**Prescription** — `/prescription`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/prescription/{token}` | Add a prescription |
| GET | `/prescription/{appointmentId}/{token}` | Get prescription for an appointment |

## Known Limitations / Roadmap

- **`application.properties` currently has a hardcoded DB password and JWT
  secret committed to the repo.** This is fine for local coursework use but is
  a real security issue for any public or production deployment — these should
  be moved to environment variables (Spring supports `${VAR_NAME}` placeholders
  in properties files) before deploying this anywhere publicly accessible.
- No automated test suite beyond the default Spring Boot test scaffold.
- No Docker Compose file yet to spin up the app alongside MySQL/MongoDB in one
  command — currently requires both databases to be set up separately.
- Token validation checks token existence per role but doesn't yet implement
  refresh tokens or expiration renewal — tokens are valid for 7 days with no
  refresh path.

## License

See `LICENSE`.
