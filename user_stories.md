# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

# Admin User Stories
User Story 1: Admin Login

**Title:**
_As an Admin, I want to log in to the portal using my username and password so that I can manage the platform securely._

**Acceptance Criteria:**

    Admin can log in using valid credentials.
    Invalid credentials must be rejected with an error message.
    Successful login must create a valid authenticated session/token.
    Only authenticated admins can access admin features.

**Priority: High**
**Story Points: 5**

User Story 2: Admin Logout

**Title:**
_As an Admin, I want to log out of the portal so that system access is protected after my session ends._

**Acceptance Criteria:**

    Admin can log out successfully at any time.
    Active session/token must be invalidated after logout.
    Logged-out admin must not access protected endpoints.
    Admin must be redirected to the login page after logout.

**Priority: High
Story Points: 2**

User Story 3: Add Doctor Profile

**Title:**
_As an Admin, I want to add doctors to the portal so that they can provide medical services on the platform._

**Acceptance Criteria:**

    Admin can add a doctor with required details (name, specialization, contact info).
    System must validate all mandatory fields before saving.
    Newly added doctor must be stored in the database.
    Doctor must be visible in the doctor list after creation.

**Priority: High
Story Points: 4**

User Story 4: Delete Doctor Profile

**Title:**
_As an Admin, I want to delete a doctor’s profile from the portal so that inactive or invalid accounts can be removed._

**Acceptance Criteria:**

    Admin can delete an existing doctor profile.
    System must confirm deletion before removing the profile.
    Deleted doctor must no longer appear in the portal.
    System must handle cases where the doctor does not exist.

**Priority: Medium
Story Points: 3**

User Story 5: View Appointment Statistics Using Stored Procedure

**Title:**
_As an Admin, I want to run a stored procedure in MySQL to get the number of appointments per month so that I can track platform usage statistics._
    
**Acceptance Criteria:**
    
    Admin can execute a predefined MySQL stored procedure.
    Stored procedure must return monthly appointment counts.
    Results must be displayed or logged in a readable format.
    Only authorized admins can execute this operation.

**Priority: Medium
Story Points: 5**

# Patient User Stories
User Story 1: View Doctors Without Login

**Title:**
_As a Patient, I want to view a list of doctors without logging in so that I can explore available options before registering._

**Acceptance Criteria:**
    
    Patient can view the list of doctors without authentication.
    Doctor details must include name, specialization, and availability.
    System must restrict booking actions for unauthenticated users.
    Doctor list must be retrieved from the database.

**Priority: Medium
Story Points: 3**

User Story 2: Patient Registration

**Title:**
_As a Patient, I want to sign up using my email and password so that I can book appointments._

**Acceptance Criteria:**

    Patient can register using a valid email and password.
    System must validate email uniqueness.
    Password must be stored securely using hashing.
    Successful registration must create a patient account.

**Priority: High
Story Points: 5**

User Story 3: Patient Login

**Title:**
_As a Patient, I want to log in to the portal so that I can manage my bookings._

**Acceptance Criteria:**
    
    Patient can log in using valid credentials.
    Invalid login attempts must return an error message.
    Successful login must generate an authenticated session/token.
    Only logged-in patients can access booking features.

**Priority: High
Story Points: 5**

User Story 4: Patient Logout

**Title:**
_As a Patient, I want to log out of the portal so that my account remains secure._

**Acceptance Criteria:**

    Patient can log out successfully.
    Active session/token must be invalidated after logout.
    Logged-out users must be redirected to the login page.
    Protected resources must not be accessible after logout.

**Priority: Medium
Story Points: 2**

User Story 5: Book Appointment with Doctor

**Title:**
_As a Patient, I want to log in and book an hour-long appointment with a doctor so that I can consult them._

**Acceptance Criteria:**

    Patient must be authenticated to book an appointment.
    Patient can select a doctor and an available time slot.
    Appointment duration must be fixed to one hour.
    System must prevent double booking of the same slot.
    Appointment details must be stored in the database.`

**Priority: High
Story Points: 6**

# Doctor User Stories
User Story 1: Doctor Login

**Title:**
_As a Doctor, I want to log in to the portal so that I can manage my appointments._

**Acceptance Criteria:**
    
    Doctor can log in using valid credentials.
    Invalid credentials must be rejected with an error message.
    Successful login must create an authenticated session/token.
    Only authenticated doctors can access doctor features.

**Priority: High
Story Points: 5**

User Story 2: Doctor Logout

**Title:**
_As a Doctor, I want to log out of the portal so that my data remains protected._

**Acceptance Criteria:**
    
    Doctor can log out successfully.
    Active session/token must be invalidated after logout.
    Logged-out doctors must not access protected resources.
    Doctor must be redirected to the login page.

**Priority: Medium
Story Points: 2**

User Story 3: View Appointment Calendar

**Title:**
_As a Doctor, I want to view my appointment calendar so that I can stay organized._

**Acceptance Criteria:**
    
    Doctor must be logged in to view the calendar.
    Calendar must display upcoming appointments.
    Appointment details must include date, time, and patient name.
    Calendar must update in real time when appointments change.

**Priority: High
Story Points: 4**

User Story 4: Mark Unavailability

**Title:**
_As a Doctor, I want to mark my unavailable time slots so that patients can book appointments only during available hours._

**Acceptance Criteria:**
    
    Doctor can mark specific time slots as unavailable.
    Unavailable slots must not be shown to patients during booking.
    System must prevent bookings during unavailable slots.
    Changes must be saved and reflected immediately.

**Priority: High
Story Points: 5**

User Story 5: Update Doctor Profile

**Title:**
_As a Doctor, I want to update my profile with specialization and contact information so that patients have up-to-date details._

**Acceptance Criteria:**
    
    Doctor can update specialization and contact details.
    Mandatory fields must be validated before saving.
    Updated information must be visible to patients.
    Changes must persist in the database.

**Priority: Medium
Story Points: 3**