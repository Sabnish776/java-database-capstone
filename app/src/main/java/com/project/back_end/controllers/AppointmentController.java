package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private Service service;

// 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String,Object>> getAppointments(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @PathVariable String patientName, @PathVariable String token){
        ResponseEntity<Map<String,String>> validation = service.validateToken(token,"doctor");
        if(validation.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity<>(Map.of("message","Unauthorized access") , HttpStatus.UNAUTHORIZED);
        }
        Map<String , Object> appointments =  appointmentService.getAppointments(patientName,date,token) ;
        return ResponseEntity.ok(appointments);
    }
// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
    @PostMapping("/{token}")
    public ResponseEntity<Map<String,String>> bookAppointment(@PathVariable String token , @RequestBody Appointment appointment){
        ResponseEntity<Map<String,String>> validation = service.validateToken(token,"patient");
        if(validation.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity<>(Map.of("message","Unauthorized access") , HttpStatus.UNAUTHORIZED);
        }
        int validationCheck = service.validateAppointment(appointment) ;
        if(validationCheck == -1){
            return new ResponseEntity<>(Map.of("message","Doctor not found") , HttpStatus.NOT_FOUND);
        }
        if(validationCheck == 0){
            return new ResponseEntity<>(Map.of("message","Appointment Slot unavailable") , HttpStatus.BAD_REQUEST);
        }
        int bookingStatus = appointmentService.bookAppointment(appointment);
        if(bookingStatus == 0){
            return new ResponseEntity<>(Map.of("message","Appointment Booking failed"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Map.of("message","Appointment Booked Successfully"), HttpStatus.OK);
    }


// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.
    @PutMapping("/{token}")
    public ResponseEntity<Map<String,String>> updateAppointment(@PathVariable String token , @RequestBody Appointment appointment){

        ResponseEntity<Map<String,String>> validation = service.validateToken(token,"patient");
        if(validation.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity<>(Map.of("message","Unauthorized access") , HttpStatus.UNAUTHORIZED);
        }
        int validationCheck = service.validateAppointment(appointment) ;
        if(validationCheck == -1){
            return new ResponseEntity<>(Map.of("message","Doctor not found") , HttpStatus.NOT_FOUND);
        }
        if(validationCheck == 0){
            return new ResponseEntity<>(Map.of("message","Appointment Slot unavailable") , HttpStatus.BAD_REQUEST);
        }
        return appointmentService.updateAppointment(appointment);
    }
// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String,String>> deleteAppointment(@PathVariable String token , @PathVariable Long id){
        ResponseEntity<Map<String,String>> validation = service.validateToken(token,"patient");
        if(validation.getStatusCode() != HttpStatus.OK){
            return new ResponseEntity<>(Map.of("message","Unauthorized access") , HttpStatus.UNAUTHORIZED);
        }
        return appointmentService.cancelAppointment(id,token) ;
    }

}
