# Architecture Summary

This Spring Boot application combines MVC and REST controllers to serve different 
types of clients. Thymeleaf templates render the Admin and Doctor dashboards as 
server-side HTML, while REST controllers expose JSON APIs for the Appointments, 
Patient Dashboard, and Patient Record modules — supporting future mobile or 
external clients.

The application uses two databases suited to different data shapes: MySQL stores 
structured, relational data (patients, doctors, appointments, admins) via JPA 
entities, while MongoDB stores flexible, document-based prescription records via 
Spring Data MongoDB. Regardless of entry point (Thymeleaf or REST), every 
controller delegates business logic to a shared service layer, which coordinates 
workflows and validation before calling the appropriate repository — JPA 
repositories for MySQL, or the MongoDB repository for prescriptions.

# Numbered Flow of Data and Control

1. A user accesses the system either through a Thymeleaf-rendered dashboard 
   (Admin/Doctor) or a REST API client (Appointments, Patient Dashboard, Patient Record).
2. The request is routed to either a Thymeleaf controller (for HTML views) or a 
   REST controller (for JSON responses), based on the URL and HTTP method.
3. The controller delegates the request to the service layer, which applies 
   business rules and coordinates any cross-entity logic (e.g. checking doctor 
   availability before booking).
4. The service layer calls the appropriate repository — a MySQL (JPA) repository 
   for structured data, or the MongoDB repository for prescriptions.
5. The repository interacts directly with its database: MySQL for relational data, 
   MongoDB for document-based data.
6. Retrieved data is bound into model classes — JPA `@Entity` objects for MySQL 
   records, `@Document` objects for MongoDB records.
7. The bound models are returned to the controller: passed into a Thymeleaf 
   template for HTML rendering, or serialized into JSON for a REST response.
