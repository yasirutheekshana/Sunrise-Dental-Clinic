# Sunrise Dental Clinic - Backend Documentation

This is the pure Java backend for the **Sunrise Dental Clinic Appointment and Patient Management System**, developed as a university student-level software engineering project without any third-party application frameworks.

---

## 1. Technology Stack

* **Language**: Pure Java 17+ (Compiled and tested on Java 20)
* **Standard Library**: `com.sun.net.httpserver.HttpServer`, Java Collections Framework, Java File I/O, `java.time`, `java.math.BigDecimal`
* **JSON File Storage**: Jackson Databind 2.15.2 (stored in `backend/data/*.json`)
* **Security & Hashing**: jBCrypt 0.4 (salted password hashing)
* **Testing**: JUnit 4.13.2
* **Build Tools**: Maven (`pom.xml`) and Standalone Batch Scripts (`build.bat`, `run.bat`, `test.bat`)

**Strict Restriction Adherence**: No frameworks were used (No Spring, Spring Boot, Hibernate, JPA, Jakarta EE, Quarkus, Micronaut, Express, or Node.js).

---

## 2. Architecture & Layering

The backend follows an N-tier layered architectural pattern:

```text
HTTP Request
    │
    ▼
DentalClinicServer (com.sun.net.httpserver.HttpServer)
    │
    ▼
Controller Layer (BaseController, AuthController, AppointmentController, etc.)
    │
    ▼
Service Layer (AuthService, AppointmentService, TreatmentService, BillingService)
    │
    ▼
Repository Layer (UserRepository, AppointmentRepository, TreatmentRepository, BillRepository)
    │
    ▼
JSON Persistence (users.json, appointments.json, treatments.json, bills.json)
```

### Core Packages

* `com.sunrisedental.model`: Domain entities (`User`, `Appointment`, `Treatment`, `Bill`, `AppointmentStatus`).
* `com.sunrisedental.dto`: Data Transfer Objects for requests and responses (`LoginRequest`, `SignupRequest`, `AppointmentRequest`, `BillRequest`, `ApiResponse`).
* `com.sunrisedental.repository`: File persistence handling thread-safe JSON I/O (`JsonFileRepository`, etc.).
* `com.sunrisedental.service`: Business logic, validations, conflict checks, and monetary math.
* `com.sunrisedental.controller`: HTTP routing, request parsing, and status code dispatching.
* `com.sunrisedental.server`: `DentalClinicServer`, `ServerConfig`, and `StaticFileHandler`.
* `com.sunrisedental.session`: Server-side `SessionManager` maintaining authenticated sessions.
* `com.sunrisedental.util`: Helpers for JSON, BCrypt, validation, and ID generation.
* `com.sunrisedental.exception`: Custom exception hierarchy (`ValidationException`, `ConflictException`, etc.).

---

## 3. How to Build, Test, and Run

### Option A: Using Windows Batch Scripts (Zero Maven Required)

1. **Build Backend**:
   ```cmd
   cd backend
   build.bat
   ```
2. **Run JUnit Tests**:
   ```cmd
   test.bat
   ```
3. **Start the Server**:
   ```cmd
   run.bat
   ```

### Option B: Using Maven

1. **Compile**:
   ```cmd
   mvn clean compile
   ```
2. **Run Tests**:
   ```cmd
   mvn test
   ```
3. **Run Application**:
   ```cmd
   mvn exec:java
   ```

The server starts at `http://localhost:8080`.
