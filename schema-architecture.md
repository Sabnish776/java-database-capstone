
Architecture Summary :
    This spring-bot project uses both MVC and REST controllers . MVC for admin and doctor dashboards ,
and REST for appointments , patient records and other modules . The application interacts with 2 databases ; MYSQL and 
MongoDB , mysql database for structured data for entities like doctor ,patient ,admin, appointment which are mapped
to JPA Entities and MongoDB for unstructured data document models like prescriptions .

Workflow (Step-by-Step)

User Access

    Admin / Doctor accesses the application via web UI (Dashboards).
    External clients (or frontend modules) access features via REST APIs.

MVC Flow (Admin & Doctor Dashboards)
    
    Request goes to MVC (Thymeleaf) Controller.
    Controller handles UI logic and forwards the request to the Service Layer.

REST Flow (Appointments, Patient Records, etc.)

    Request hits REST Controller (JSON-based).
    REST controller validates input and forwards the request to the Service Layer.

Service Layer (Business Logic)

    Central layer used by both MVC and REST controllers.
    Contains all business rules (appointments, patient handling, prescriptions).

Database Routing (Based on Data Type)
    
    Structured data (Doctor, Patient, Admin, Appointment):
        Service calls MySQL JPA Repositories
    
    Unstructured data (Prescriptions):
        Service calls MongoDB Repository

Persistence Layer

    MySQL repositories interact with MySQL Database using JPA Entities.
    MongoDB repository interacts with MongoDB Database using Document Models.

Response Flow
    
    Data flows back from databases → repositories → service layer.
    Returned to:
        MVC Controller → rendered as Thymeleaf view
        REST Controller → returned as JSON response

End User Output
    
    Admin/Doctor sees updated dashboard UI. 
    API consumers receive structured JSON responses.